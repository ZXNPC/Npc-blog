package com.example.blognpc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blognpc.dto.PaginationDTO;
import com.example.blognpc.dto.QuestionDTO;
import com.example.blognpc.dto.UserDTO;
import com.example.blognpc.enums.AccountIdEnum;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.enums.LoginErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.exception.LoginException;
import com.example.blognpc.mapper.UserMapper;
import com.example.blognpc.model.GithubUser;
import com.example.blognpc.model.Question;
import com.example.blognpc.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

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
            userMapper.insert(user);
            log.info("User created via github: ", user);
        } else {
            // 用户存在，更新用户
            user.setId(dbUser.getId());
            user.setToken(dbUser.getToken());
            user.setEmail(dbUser.getEmail());
            user.setPassword(password);
            user.setGmtCreate(dbUser.getGmtCreate());
            user.setGmtModified(System.currentTimeMillis());
            userMapper.updateById(user);
            log.info("User verified via github: ", user);
        }
        return user;
    }

    public User passwordSignin(String email, String originalPassword) {
        String password = DigestUtils.md5DigestAsHex(DigestUtils.md5DigestAsHex(originalPassword.getBytes(StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8));
        List<User> users = userMapper.selectList(new QueryWrapper<User>().eq("email", email));
        User dbUser = users.size() == 0 ? null : users.get(0);
        if (dbUser == null) {
            // 用户不存在，抛出一些异常信息
            throw new LoginException(LoginErrorCode.EMAIL_NOT_FOUND);
        } else {

            // 用户存在，检验密码是否正确
            if (password.equals(dbUser.getPassword())) {
                // 密码正确，返回
                return dbUser;
            } else {
                // 密码错误，返回出错
                throw new LoginException(LoginErrorCode.PASSWORD_NOT_CORRECT);
            }
        }
    }


    public PaginationDTO<UserDTO> list(Long page, Long size, String search) {
        if (StringUtils.isBlank(search)) {
            // 无搜索
            Long totalCount = userMapper.selectCount(null);
            PaginationDTO<UserDTO> paginationDTO = new PaginationDTO<UserDTO>();
            paginationDTO.setPagination(totalCount, page, size);
            page = paginationDTO.getPage();

            Long offset = (page - 1) * size;
            List<User> users = userMapper.selectList(new QueryWrapper<User>()
                    .last(String.format("limit %d, %d", offset, size)));

            List<UserDTO> userDTOS = new ArrayList<>();
            for (User user : users) {
                UserDTO userDTO = new UserDTO();
                BeanUtils.copyProperties(user, userDTO);
                userDTOS.add(userDTO);
            }

            paginationDTO.setData(userDTOS);
            return paginationDTO;
        } else {
            // 有搜索，对搜索内容进行判断，形式如 "search_type : search_content"
            // TODO: 前端对搜索的格式进行判断
            List<String> collect = Arrays.stream(search.split(":")).map(s -> s = s.trim()).collect(Collectors.toList());
            String searchType = collect.get(0);
            String searchContent = collect.get(1);

            Long totalCount = userMapper.selectCount(new QueryWrapper<User>().eq(searchType, searchContent));
            PaginationDTO<UserDTO> paginationDTO = new PaginationDTO<>();
            paginationDTO.setPagination(totalCount, page, size);
            page = paginationDTO.getPage();

            Long offset = (page - 1) * size;
            List<User> users = userMapper.selectList(new QueryWrapper<User>()
                    .eq(searchType, searchContent)
                    .last(String.format("limit %d, %d", offset, size)));

            List<UserDTO> userDTOS = new ArrayList<>();
            for (User user : users) {
                UserDTO userDTO = new UserDTO();
                BeanUtils.copyProperties(user, userDTO);
                userDTOS.add(userDTO);
            }

            paginationDTO.setData(userDTOS);
            return paginationDTO;
        }
    }
}
