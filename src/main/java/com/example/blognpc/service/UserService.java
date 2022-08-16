package com.example.blognpc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blognpc.cache.UserWaitingMap;
import com.example.blognpc.dto.ResultDTO;
import com.example.blognpc.enums.LoginErrorCode;
import com.example.blognpc.mapper.UserMapper;
import com.example.blognpc.model.GithubUser;
import com.example.blognpc.model.User;
import com.example.blognpc.model.UserWaiting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User saveOrUpdate(GithubUser githubUser) {
        User user = new User();
        user.setAccountId(githubUser.getId());
        user.setAvatarUrl(githubUser.getAvatarUrl());
        user.setBio(githubUser.getBio());
        user.setName(githubUser.getName() == null ? githubUser.getLogin() : githubUser.getName());
        List<User> users = userMapper.selectList(new QueryWrapper<User>().eq("account_id", user.getAccountId()));
        User dbUser = users.size() == 0 ? null : users.get(0);
        if (dbUser == null) {
            // 用户不存在，创建用户
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setToken(UUID.randomUUID().toString());
            userMapper.insert(user);
        } else {
            // 用户存在，更新用户
            user.setId(dbUser.getId());
            user.setGmtCreate(dbUser.getGmtCreate());
            user.setGmtModified(System.currentTimeMillis());
            user.setToken(dbUser.getToken());
            userMapper.updateById(user);
        }
        return user;
    }

    public Object passwordLogin(String email, String orginalPassword) {
        String password = DigestUtils.md5DigestAsHex(DigestUtils.md5DigestAsHex(orginalPassword.getBytes(StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8));
        List<User> users = userMapper.selectList(new QueryWrapper<User>().eq("email", email));
        User userdb = users.size() == 0 ? null : users.get(0);
        if (userdb == null) {
            // 用户不存在，抛出一些异常信息
            return ResultDTO.errorOf(LoginErrorCode.EMAIL_NOT_FOUND);
        }
        else {
            // 用户存在，检验密码是否正确
            if (password.equals(userdb.getPassword()))
                return userdb;
            else
                return ResultDTO.errorOf(LoginErrorCode.PASSWORD_NOT_CORRECT);
        }
    }

    public Object checkEmail(String email) {
        List<User> users = userMapper.selectList(new QueryWrapper<User>().eq("email", email));
        User userdb = users.size() == 0 ? null : users.get(0);
        if (userdb == null) {
            return null;
        }
        else {
            return ResultDTO.errorOf(LoginErrorCode.EMAIL_FOUND);
        }
    }

    public User saveOrUpdate(String token, UserWaiting userWaiting) {
        User user = new User();
        user.setEmail(userWaiting.getEmail());
        user.setToken(token);
        user.setName(userWaiting.getName());
        user.setBio(null);
        user.setGmtCreate(System.currentTimeMillis());
        user.setGmtModified(user.getGmtCreate());
        user.setAccountId();
        user.setAvatarUrl(null);
        user.setPassword(null);
    }
}
