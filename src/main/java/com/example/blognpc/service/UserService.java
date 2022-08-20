package com.example.blognpc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blognpc.dto.ResultDTO;
import com.example.blognpc.enums.AccountIdEnum;
import com.example.blognpc.enums.LoginErrorCode;
import com.example.blognpc.exception.LoginException;
import com.example.blognpc.mapper.UserMapper;
import com.example.blognpc.model.GithubUser;
import com.example.blognpc.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserService {
    @Value("${signup.user.expiration}")
    private Long expiration;
    @Autowired
    private UserMapper userMapper;

    public User saveOrUpdate(GithubUser githubUser) {
        User user = new User();
        user.setAccountId(githubUser.getId());
        user.setName(githubUser.getName() == null ? githubUser.getLogin() : githubUser.getName());
        user.setBio(githubUser.getBio());
        user.setAvatarUrl(githubUser.getAvatarUrl());
        List<User> users = userMapper.selectList(new QueryWrapper<User>().eq("account_id", user.getAccountId()));
        User dbUser = users.size() == 0 ? null : users.get(0);
        String token = UUID.randomUUID().toString();
        String password = DigestUtils.md5DigestAsHex(DigestUtils.md5DigestAsHex(token.getBytes(StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8));
        if (dbUser == null) {
            // 用户不存在，创建用户
            user.setEmail(githubUser.getEmail());
            user.setToken(token);
            user.setPassword(password);
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setComplete(true);
            userMapper.insert(user);
            log.info("User created via github: ", user);
        } else {
            // 用户存在，更新用户
            user.setId(dbUser.getId());
            user.setToken(dbUser.getToken());
            user.setGmtCreate(dbUser.getGmtCreate());
            user.setGmtModified(System.currentTimeMillis());
            user.setComplete(true);
            userMapper.updateById(user);
        }
        return user;
    }

    public Object passwordLogin(String email, String originalPassword) {
        String password = DigestUtils.md5DigestAsHex(DigestUtils.md5DigestAsHex(originalPassword.getBytes(StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8));
        List<User> users = userMapper.selectList(new QueryWrapper<User>().eq("email", email));
        User userdb = users.size() == 0 ? null : users.get(0);
        if (userdb == null) {
            // 用户不存在，抛出一些异常信息
            return ResultDTO.errorOf(LoginErrorCode.EMAIL_NOT_FOUND);
        } else {
            if (userdb.getComplete() == false) {
                // 用户信息不完整（未验证邮箱和设置密码），返回错误信息
                return ResultDTO.errorOf(LoginErrorCode.EMAIL_UNVERIFIED);
            }

            // 用户存在，检验密码是否正确
            if (password.equals(userdb.getPassword())) {
                // 密码正确，返回
                return userdb;
            } else {
                // 密码错误，返回出错
                return ResultDTO.errorOf(LoginErrorCode.PASSWORD_NOT_CORRECT);
            }
        }
    }

    public Object saveByEmail(String userName, String email, String token) {
        List<User> users = userMapper.selectList(new QueryWrapper<User>().eq("email", email));
        User userdb = users.size() == 0 ? null : users.get(0);
        if (userdb == null) {
            User user = new User();
            user.setAccountId(AccountIdEnum.DEFAULT_ACCOUNT_ID.getCode());
            user.setName(userName);
            user.setEmail(email);
            user.setPassword("");
            user.setBio(null);
            user.setAvatarUrl("https://avatars.dicebear.com/api/identicon/" + user.getName() + ".svg");
            user.setToken(token);
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setComplete(false);
            userMapper.insert(user);
            return user;
        } else {
            if (userName == null) {
                // 用户名为空，说明直接来自于 js post
                userdb.setGmtModified(System.currentTimeMillis());
                userMapper.updateById(userdb);
                return userdb;
            }
            if (userdb.getComplete() == false) {
                // 邮箱未验证
                return ResultDTO.errorOf(LoginErrorCode.EMAIL_UNVERIFIED);
            } else {
                return ResultDTO.errorOf(LoginErrorCode.EMAIL_FOUND);
            }
        }
    }

    public User UpdateByPassword(String token, String originalPassword) {
        String password = DigestUtils.md5DigestAsHex(DigestUtils.md5DigestAsHex(originalPassword.getBytes(StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8));
        List<User> users = userMapper.selectList(new QueryWrapper<User>().eq("token", token));
        User user = users.size() == 0 ? null : users.get(0);
        if (user == null) {
            // 不知道为什么，反正就是找不到用户
            throw new LoginException(LoginErrorCode.TOKEN_NOT_FOUND);
        }
        else {
            // 正常来说找到了
            user.setPassword(password);
            user.setGmtModified(System.currentTimeMillis());
            user.setComplete(true);
            userMapper.updateById(user);
            return user;
        }
    }

    public Object verifyExpiration(String token, Long expirationTime) {
        List<User> users = userMapper.selectList(new QueryWrapper<User>().eq("token", token));
        User user = users.size() == 0 ? null : users.get(0);
        if (user == null) {
            // 用户未找到或者信息已经被清除
            return ResultDTO.errorOf(LoginErrorCode.TOKEN_NOT_FOUND);
        } else {
            if (user.getComplete() == true) {
                // 用户信息已完整，说明已验证用户重新点击链接进行验证
                // TODO:也许我可以在用户多次点击链接的情况下做一个小彩蛋
                return ResultDTO.errorOf(LoginErrorCode.EMAIL_ALREADY_VERIFIED);
            }
            // 用户找到了，注意已验证的用户再次点击链接验证
            if (user.getGmtModified() + expiration != expirationTime) {
                // 过期时间不匹配，说明链接遭到篡改
                return ResultDTO.errorOf(LoginErrorCode.EXPIRATION_UNCORRECT);
            } else {
                // 过期时间匹配
                if (System.currentTimeMillis() >= expirationTime) {
                    // 链接已经过期（但是还没到定时刷新数据库删除的时候）
                    return ResultDTO.errorOf(LoginErrorCode.TOKEN_REACH_EXPIRATION);
                } else {
                    // 链接未过期
                    return user;
                }
            }
        }
    }
}
