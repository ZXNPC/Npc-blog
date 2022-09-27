package com.example.blognpc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blognpc.enums.AccountIdEnum;
import com.example.blognpc.enums.LoginErrorCode;
import com.example.blognpc.enums.VerifyErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.exception.LoginException;
import com.example.blognpc.exception.VerifyException;
import com.example.blognpc.mapper.UserMapper;
import com.example.blognpc.mapper.UserUnverifiedMapper;
import com.example.blognpc.model.User;
import com.example.blognpc.model.UserUnverified;
import com.example.blognpc.utils.CalendarUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserUnverifiedService {
    @Value("${signup.user.expiration}")
    private Long expiration;
    @Autowired
    private UserUnverifiedMapper userUnverifiedMapper;
    @Autowired
    private UserMapper userMapper;

    public void saveByEmail(UserUnverified user) {
        user.setPassword(DigestUtils.md5DigestAsHex(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8)));
        List<UserUnverified> users = userUnverifiedMapper.selectList(new QueryWrapper<UserUnverified>().eq("email", user.getEmail()));
        UserUnverified dbUser = users.size() == 0 ? null : users.get(0);
        if (userMapper.selectList(new QueryWrapper<User>().eq("email", user.getEmail())).size() == 0) {
            if (dbUser == null) {
                // 用户不存在，可以创建新用户
                user.setGmtCreate(System.currentTimeMillis());
                user.setGmtModified(user.getGmtCreate());
                userUnverifiedMapper.insert(user);
            } else {
                // 邮箱未被注册
                if (user.getPassword() == null) {
                    // 没有密码且没有被前端拦截，说明是来自于 js 的跳转，重新发送邮件
                    user.setGmtCreate(dbUser.getGmtCreate());
                    user.setId(dbUser.getId());
                    userUnverifiedMapper.updateById(user);
                } else {
                    // 用户存在，且邮箱未验证，检查用户是否过期
                    long currentTimeMillis = System.currentTimeMillis();
                    if (dbUser.getGmtModified() > currentTimeMillis) {
                        // 用户过期，删除并抛异常
                        userUnverifiedMapper.deleteById(dbUser);
                        throw new LoginException(LoginErrorCode.TOKEN_REACH_EXPIRATION);
                    } else {
                        // 用户未过期，抛未验证异常
                        throw new LoginException(LoginErrorCode.EMAIL_UNVERIFIED_SIGNUP);
                    }
                }
            }
        }
        else {
            throw new LoginException(LoginErrorCode.EMAIL_FOUND);
        }
    }

//    public User verifyExpiration(String token, String userName, String email, Long expirationTime) {
//        List<User> users = userMapper.selectList(new QueryWrapper<User>().eq("token", token));
//        User user = users.size() == 0 ? null : users.get(0);
//        if (user == null) {
//            // 用户未找到或者信息已经被清除
//            throw new LoginException(LoginErrorCode.TOKEN_NOT_FOUND);
//        } else {
//            if (user.getComplete() == true) {
//                // 用户信息已完整，说明已验证用户重新点击链接进行验证
//                throw new LoginException(LoginErrorCode.EMAIL_ALREADY_VERIFIED);
//            }
//            // 用户找到了，注意已验证的用户再次点击链接验证
//            if (user.getGmtModified() + expiration != expirationTime || !email.equals(user.getEmail())) {
//                // 过期时间不匹配，说明链接遭到篡改
//                throw new LoginException(LoginErrorCode.LINK_NOT_CORRECT);
//            } else {
//                // 过期时间匹配
//                if (System.currentTimeMillis() >= expirationTime) {
//                    // 链接已经过期（但是还没到定时刷新数据库删除的时候）
//                    throw new LoginException(LoginErrorCode.TOKEN_REACH_EXPIRATION);
//                } else {
//                    // 链接未过期
//                    return user;
//                }
//            }
//        }
//    }

    public UserUnverified verifyExpiration(String token, String email, Long expirationTime) {
        List<UserUnverified> users = userUnverifiedMapper.selectList(new QueryWrapper<UserUnverified>()
                .eq("token", token)
                .eq("email", email)
        );
        UserUnverified user = users.size() == 0 ? null : users.get(0);
        if (user == null) {
            // 用户未找到，说明已经被清除
            throw new LoginException(LoginErrorCode.TOKEN_REACH_EXPIRATION);
        } else {
            // 用户找到
            if (userMapper.selectList(new QueryWrapper<User>().eq("email", user.getEmail())).size() != 0) {
                // 用户邮箱已注册
                throw new LoginException(LoginErrorCode.EMAIL_FOUND);
            } else {
                // 用户邮箱未注册，继续
                if (user.getGmtModified() + expiration != expirationTime) {
                    // 过期时间不匹配，链接遭到修改
                    throw new LoginException(LoginErrorCode.LINK_NOT_CORRECT);
                } else {
                    // 过期时间匹配
                    if (System.currentTimeMillis() > expirationTime) {
                        // 链接过期
                        userUnverifiedMapper.deleteById(user);
                        throw new LoginException(LoginErrorCode.TOKEN_REACH_EXPIRATION);
                    } else {
                        // 链接未过期
                        return user;
                    }
                }
            }
        }
    }

//    public User UpdateByPassword(String token, String originalPassword) {
//        String password = DigestUtils.md5DigestAsHex(DigestUtils.md5DigestAsHex(originalPassword.getBytes(StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8));
//        List<User> users = userMapper.selectList(new QueryWrapper<User>().eq("token", token));
//        User user = users.size() == 0 ? null : users.get(0);
//        if (user == null) {
//            // 不知道为什么，反正就是找不到用户
//            throw new LoginException(LoginErrorCode.TOKEN_NOT_FOUND);
//        } else {
//            // 正常来说找到了
//            user.setPassword(password);
//            user.setGmtModified(System.currentTimeMillis());
//            user.setComplete(true);
//            userMapper.updateById(user);
//            return user;
//        }
//    }

    public User UpdateByPassword(String userName, String email, String token, String originalPassword) {
        String password = DigestUtils.md5DigestAsHex(DigestUtils.md5DigestAsHex(originalPassword.getBytes(StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8));
        List<UserUnverified> users = userUnverifiedMapper.selectList(new QueryWrapper<UserUnverified>()
                .eq("token", token)
                .eq("email", email));
        UserUnverified userUnverified = users.size() == 0 ? null : users.get(0);
        if (userUnverified == null) {
            // 反正就是找不到
            throw new VerifyException(VerifyErrorCode.TOKEN_REACH_EXPIRATION);
        } else {
            // 找到了
            if (!userUnverified.getPassword().equals(password)) {
                // 密码不正确
                throw new VerifyException(VerifyErrorCode.PASSWORD_ERROR);
            } else {
                // 密码正确
                User user = new User();
                BeanUtils.copyProperties(userUnverified, user);
                user.setAccountId(AccountIdEnum.DEFAULT_ACCOUNT_ID.getCode());
                user.setName(userName);
                user.setBio(null);
                user.setAvatarUrl("https://avatars.dicebear.com/api/identicon/" + user.getName() + CalendarUtils.getCreateDay(user.getGmtCreate()) + ".svg");
                user.setGmtModified(System.currentTimeMillis());
                userMapper.insert(user);
                userUnverifiedMapper.deleteById(userUnverified);
                return user;
            }
        }
    }

//    public void removeExpired() {
//        Long currentTime = System.currentTimeMillis();
//        List<User> users = userMapper.selectList(null);
//        List<Long> userIds = users.stream().filter(user -> user.getComplete() == false).map(user -> user.getId()).collect(Collectors.toList());
//        if (!userIds.isEmpty()) {
//            userMapper.delete(new QueryWrapper<User>().in("id", userIds).gt("gmt_create", currentTime - expiration));
//        }
//    }

    public void removeExpired() {
        long currentTimeMillis = System.currentTimeMillis();
        List<UserUnverified> users = userUnverifiedMapper.selectList(null);
        List<Long> userIds = users.stream().filter(user -> user.getGmtModified() + expiration > currentTimeMillis).map(user -> user.getId()).collect(Collectors.toList());
        if (!userIds.isEmpty()) {
            userUnverifiedMapper.deleteBatchIds(userIds);
        }
    }

    public void deleteByEmail(UserUnverified user) {
        userUnverifiedMapper.delete(new QueryWrapper<UserUnverified>().eq("email", user.getEmail()));
    }
}
