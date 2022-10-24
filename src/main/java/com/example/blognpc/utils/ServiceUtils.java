package com.example.blognpc.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.example.blognpc.dto.*;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.mapper.*;
import com.example.blognpc.model.*;
import com.example.blognpc.service.DraftService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class ServiceUtils {
    private static ServiceUtils serviceUtils;

    @Autowired
    private UserMapper userMapper;

    @PostConstruct
    public void init() {
        serviceUtils = this;
        serviceUtils.userMapper = this.userMapper;
    }

    public static Map<Long, User> getUserMap(Set<Long> creatorSet) {
        // 如果用户数量为空，则添加一个 0 ，防止 sql 语句错误
        if (creatorSet.size() == 0) creatorSet.add(0L);
        List<Long> creatorList = new ArrayList<>();
        creatorList.addAll(creatorSet);
        List<User> userList = serviceUtils.userMapper.selectList(new QueryWrapper<User>().in("id", creatorList));
        Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));
        return userMap;
    }

    public static Boolean matchesSearchPattern(String search) {
        String pattern = "(\\w+:\".+\")((\\s+(and)|(or)|(AND)|(OR)\\s+)(\\w+:\".+\"))*";
        return Pattern.matches(pattern, search);
    }

//    public static void doSearch(List<String> searchType, List<String> searchContent, String search, String defaultType) {
//        if (matchesSearchPattern(search)) {
//            // 使用高级搜索
//            List<String> searchList = Arrays.stream(search.split("(and)|(or)|(AND)|(OR)")).map(s -> s = s.trim()).collect(Collectors.toList());
//            for (String sss : searchList) {
//                List<String> ss = Arrays.stream(sss.split(":")).map(s -> s = s.trim()).collect(Collectors.toList());
//                searchType.add(ss.get(0));
//                searchContent.add(ss.get(1).replace("\"", ""));
//            }
//
//        } else {
//            // 使用普通搜索
//            searchType.add(defaultType);
//            searchContent.add(search);
//        }
//    }
}
