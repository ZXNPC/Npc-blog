package com.example.blognpc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blognpc.dto.ResultDTO;
import com.example.blognpc.enums.AccountIdEnum;
import com.example.blognpc.enums.LoginErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.mapper.UserMapper;
import com.example.blognpc.model.GithubUser;
import com.example.blognpc.model.User;
import com.example.blognpc.model.UserWaiting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User saveOrUpdate(GithubUser githubUser) {
        User user = new User();
        user.setAccountId(githubUser.getId());
        user.setName(githubUser.getName() == null ? githubUser.getLogin() : githubUser.getName());
        user.setEmail(githubUser.getEmail());
        user.setPassword(UUID.randomUUID().toString());
        user.setBio(githubUser.getBio());
        user.setAvatarUrl(githubUser.getAvatarUrl());
        List<User> users = userMapper.selectList(new QueryWrapper<User>().eq("account_id", user.getAccountId()));
        User dbUser = users.size() == 0 ? null : users.get(0);
        if (dbUser == null) {
            // 用户不存在，创建用户
            user.setToken(UUID.randomUUID().toString());
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
        }
        else {
            if (userdb.getComplete() == false) {
                // 用户信息不完整（未验证邮箱和设置密码），返回错误信息
                return ResultDTO.errorOf(LoginErrorCode.EMAIL_UNVERIFIED);
            }

            // 用户存在，检验密码是否正确
            if (password.equals(userdb.getPassword())) {
                // 密码正确，返回
                return userdb;
            }
            else {
                // 密码错误，返回出错
                return ResultDTO.errorOf(LoginErrorCode.PASSWORD_NOT_CORRECT);
            }
        }
    }

    public User saveOrUpdate(UserWaiting userWaiting, String token, String originalPassword) {
        String password = DigestUtils.md5DigestAsHex(DigestUtils.md5DigestAsHex(originalPassword.getBytes(StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8));
        List<User> users = userMapper.selectList(new QueryWrapper<User>().eq("token", token));
        User userdb = users.size() == 0 ? null : users.get(0);
        if (userdb == null) {
            User user = new User();
            user.setEmail(userWaiting.getEmail());
            user.setToken(token);
            user.setName(userWaiting.getName());
            user.setBio(null);
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setAccountId(AccountIdEnum.DEFAULT_ACCOUNT_ID.getCode());
            user.setAvatarUrl("https://avatars.dicebear.com/api/identicon/" + user.getName() + ".svg");
            user.setPassword(password);
            return user;
        }
        else {
            // 不应该存在
            throw new CustomizeException(LoginErrorCode.EMAIL_FOUND);
        }
    }

    public Object saveEmail(String userName, String email, String token) {
        List<User> users = userMapper.selectList(new QueryWrapper<User>().eq("email", email));
        User userdb = users.size() == 0 ? null : users.get(0);
        if (userdb == null) {
            User user = new User();
            user.setAccountId(AccountIdEnum.DEFAULT_ACCOUNT_ID.getCode());
            user.setName(userName);
            user.setEmail(email);
            user.setBio("");
            user.setAvatarUrl("https://avatars.dicebear.com/api/identicon/" + user.getName() + ".svg");
            user.setToken(token);
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setComplete(false);
            userMapper.insert(user);
            return user;
        } else {
            if (userdb.getComplete() == false) {
                return ResultDTO.errorOf(LoginErrorCode.EMAIL_UNVERIFIED);
            }
            else {
                return ResultDTO.errorOf(LoginErrorCode.EMAIL_FOUND);
            }
        }
    }

    public User Update(String token, String password) {
        List<User> users = userMapper.selectList(new QueryWrapper<User>().eq("token", token));
    }
}
