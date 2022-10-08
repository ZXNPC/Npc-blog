package com.example.blognpc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blognpc.dto.PaginationDTO;
import com.example.blognpc.dto.UserDTO;
import com.example.blognpc.enums.LoginErrorCode;
import com.example.blognpc.exception.LoginException;
import com.example.blognpc.mapper.UserMapper;
import com.example.blognpc.model.*;
import com.example.blognpc.provider.SearchProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.BadSqlGrammarException;
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
    @Autowired
    private SearchProvider searchProvider;

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


    public PaginationDTO<UserDTO> list(Long page, Long size, String search, String orderDesc) {
        if (StringUtils.isBlank(search)) {
            // 不使用搜索
            Long totalCount = userMapper.selectCount(null);
            PaginationDTO<UserDTO> paginationDTO = new PaginationDTO<UserDTO>();
            paginationDTO.setPagination(totalCount, page, size);
            page = paginationDTO.getPage();

            Long offset = (page - 1) * size;
            List<User> users = userMapper.selectList(new QueryWrapper<User>()
                    .last(String.format("limit %d, %d", offset, size)));

            return getUserDTOPaginationDTO(paginationDTO, users);
        } else {
            // 使用搜索
            String regexp = searchProvider.generateRegexp(search, "name");

            QueryWrapper<User> countWrapper = new QueryWrapper<User>()
                    .apply(regexp);

            Long totalCount = null;
            try {
                totalCount = userMapper.selectCount(countWrapper);
            } catch (BadSqlGrammarException e) {
                return null;
            }
            PaginationDTO<UserDTO> paginationDTO = new PaginationDTO<>();
            paginationDTO.setPagination(totalCount, page, size);
            page = paginationDTO.getPage();

            Long offset = (page - 1) * size;
            QueryWrapper<User> selectWrapper = new QueryWrapper<User>()
                    .apply(regexp)
                    .orderByDesc(StringUtils.isNotBlank(orderDesc), orderDesc)
                    .last(String.format("limit %d, %d", offset, size));
            List<User> users = userMapper.selectList(selectWrapper);

            return getUserDTOPaginationDTO(paginationDTO, users);
        }
    }

    private PaginationDTO<UserDTO> getUserDTOPaginationDTO(PaginationDTO<UserDTO> paginationDTO, List<User> users) {
        List<UserDTO> userDTOS = users.stream().map(user -> {
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO);
            return userDTO;
        }).collect(Collectors.toList());

        paginationDTO.setData(userDTOS);
        return paginationDTO;
    }
}
