package com.example.blognpc.provider;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blognpc.mapper.UserMapper;
import com.example.blognpc.model.User;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SearchProvider {
    @Autowired
    private UserMapper userMapper;

    public String generateRegexp(String search, String defaultType) {
        List<String> type;
        List<String> content;
        List<String> conj;
        String pattern = "(\\w+:\".+\")((\\s+(and)|(or)|(AND)|(OR)\\s+)(\\w+:\".+\"))*";
        type = new ArrayList<>();
        content = new ArrayList<>();
        conj = new ArrayList<>();
        if (Pattern.matches(pattern, search)) {
            // 高级检索
            conj.addAll(Arrays.stream(search.split(" ")).filter(s -> Pattern.matches("(and)|(or)|(AND)|(OR)", s)).collect(Collectors.toList()));
            String[] sss = search.split("(and)|(or)|(AND)|(OR)");
            for (String ss : sss) {
                List<String> collect = Arrays.stream(ss.split(":")).map(s -> s = s.trim()).collect(Collectors.toList());
                type.add(collect.get(0));
                content.add(collect.get(1).replace("\"", ""));
            }
            if (type.size() != content.size() || type.size() != conj.size() + 1) {
                // 错误，重回普通检索
                type.clear();
                content.clear();
                type.add(defaultType);
                content.add(search);
            }
        } else {
            // 普通检索
            type.add(defaultType);
            content.add(search);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < type.size(); i++) {
            if (i != 0) {
                stringBuilder.append(" ");
                stringBuilder.append(conj.get(i - 1));
            }
            if (type.get(i).equals("user")) {
                List<User> users = userMapper.selectList(new QueryWrapper<User>().apply(" name regexp '" + content.get(i) + "' "));
                Set<String> creatorSet;
                if (users.size() == 0) {
                    creatorSet = new HashSet<>();
                } else {
                    creatorSet = users.stream().map(user -> user.getId().toString()).collect(Collectors.toSet());
                }
                String creatorRegexp = creatorSet.stream().collect(Collectors.joining("|"));
                stringBuilder.append(" ");
                stringBuilder.append("creator");
                stringBuilder.append(" regexp ");
                stringBuilder.append(creatorRegexp);
                stringBuilder.append(" ");
            } else {
                stringBuilder.append(" ");
                stringBuilder.append(type.get(i));
                stringBuilder.append(" regexp ");
                stringBuilder.append("'" + content.get(i) + "'");
                stringBuilder.append(" ");
            }
        }
        return stringBuilder.toString();
    }
}
