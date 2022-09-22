//package com.example.blognpc.utils;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.example.blognpc.dto.*;
//import com.example.blognpc.enums.CustomizeErrorCode;
//import com.example.blognpc.exception.CustomizeException;
//import com.example.blognpc.mapper.*;
//import com.example.blognpc.model.*;
//import com.example.blognpc.service.DraftService;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Service;
//
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//public class ServiceUtils<T> {
//    @Autowired
//    private UserMapper userMapper;
//    @Autowired
//    private QuestionMapper questionMapper;
//    @Autowired
//    private QuestionExtMapper questionExtMapper;
//    @Autowired
//    private ArticleMapper articleMapper;
//    @Autowired
//    private ArticleExtMapper articleExtMapper;
//    @Autowired
//    private DraftMapper draftMapper;
//    @Autowired
//    private DraftExtMapper draftExtMapper;
//
//    public PaginationDTO <T> list(Long page, Long size, Class tClass) {
//        return list(null, page, size, null, tClass);
//    }
//
//    public PaginationDTO <T> list(Long creator, Long page, Long size, Class tClass) {
//        return list(creator, page, size, null, tClass);
//    }
//
//    public PaginationDTO <T> list(Long creator, Long page, Long size, String search,Type type) {
//        // 获取泛型的 class
//        PaginationDTO<>
//
//        Long totalCount;
//        String titleRegexp;
//        if (StringUtils.isBlank(search)) {
//            // 不使用搜索
//            titleRegexp = "";
//            if (tClass.equals(QuestionDTO.class)) {
//                totalCount = questionMapper.selectCount(new QueryWrapper<Question>().eq(creator != 0L || creator != null, "creator", creator));
//            } else if (tClass.equals(ArticleDTO.class)) {
//                totalCount = articleMapper.selectCount(new QueryWrapper<Article>().eq(creator != 0L || creator != null, "creator", creator));
//            } else if (tClass.equals(DraftDTO.class)) {
//                totalCount = draftMapper.selectCount(new QueryWrapper<Draft>().eq(creator != 0L || creator != null, "creator", creator));
//            } else {
//                totalCount = 0L;
//            }
//        } else {
//            // 使用搜索
//            titleRegexp = "\'" + Arrays.stream(search.split(" ")).filter(s -> StringUtils.isNotBlank(s)).collect(Collectors.joining("|")) + "\'";
//            if (tClass.equals(QuestionDTO.class)) {
//                totalCount = questionExtMapper.selectCountRegexp(null, "title", titleRegexp);
//            } else if (tClass.equals(ArticleDTO.class)) {
//                totalCount = articleExtMapper.selectCountRegexp(creator, "title", titleRegexp);
//            } else if (tClass.equals(DraftDTO.class)) {
//                totalCount = draftExtMapper.selectCountRegexp(creator, "title", titleRegexp);
//            } else {
//                totalCount = 0L;
//            }
//        }
//
//        paginationDTO.setPagination(totalCount, page, size);
//        page = paginationDTO.getPage();
//
//        Long offset = (page - 1) * size;
//        List<T> items;
//        if (StringUtils.isBlank(titleRegexp)) {
//            // 不使用搜索
//            if (tClass.equals(QuestionDTO.class)) {
//                items = (List<T>) questionMapper.selectList(new QueryWrapper<Question>()
//                        .eq(creator != 0L, "creator", creator)
//                        .orderByDesc("gmt_create")
//                        .last(String.format("limit %d, %d", offset, size)));
//            } else if (tClass.equals(ArticleDTO.class)) {
//                items = (List<T>) articleMapper.selectList(new QueryWrapper<Article>()
//                        .eq(creator != 0L, "creator", creator)
//                        .orderByDesc("gmt_create")
//                        .last(String.format("limit %d, %d", offset, size)));
//            } else if (tClass.equals(DraftDTO.class)) {
//                items = (List<T>) draftMapper.selectList(new QueryWrapper<Draft>()
//                        .eq(creator != 0L, "creator", creator)
//                        .orderByDesc("gmt_create")
//                        .last(String.format("limit %d, %d", offset, size)));
//            } else {
//                items = new ArrayList<>();
//            }
//        } else {
//            // 使用搜索
//            if (tClass.equals(QuestionDTO.class)) {
//                items = (List<T>) questionExtMapper.selectRegexp(null, "title", titleRegexp, "gmt_create", 1, offset, size);
//            } else if (tClass.equals(ArticleDTO.class)) {
//                items = (List<T>) articleExtMapper.selectRegexp(null, "title", titleRegexp, "gmt_create", 1, offset, size);
//            } else if (tClass.equals(DraftDTO.class)) {
//                items = (List<T>) draftExtMapper.selectRegexp(null, "title", titleRegexp, "gmt_create", 1, offset, size);
//            } else {
//                items = new ArrayList<>();
//            }
//        }
////        if (tClass.equals(QuestionDTO.class)) {
////
////        } else if (tClass.equals(ArticleDTO.class)) {
////
////        } else if (tClass.equals(DraftDTO.class)) {
////
////        } else {
////
////        }
//
//        // 生成 key = creator, value = User 的 Map
//        List<Long> creatorList;
//        if (tClass.equals(QuestionDTO.class)) {
//            creatorList = items.stream().map(item -> ((Question) item).getCreator()).collect(Collectors.toList());
//        } else if (tClass.equals(ArticleDTO.class)) {
//            creatorList = items.stream().map(item -> ((Article) item).getCreator()).collect(Collectors.toList());
//        } else if (tClass.equals(DraftDTO.class)) {
//            creatorList = items.stream().map(item -> ((Draft) item).getCreator()).collect(Collectors.toList());
//        } else {
//            creatorList = new ArrayList<>();
//        }
//        // 如果用户数量为空，则添加一个 0 ，防止 sql 语句错误
//        if (creatorList.size() == 0) creatorList.add(0L);
//        List<User> userList = userMapper.selectList(new QueryWrapper<User>().in("id", creatorList));
//        Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));
//
//        List<T> itemDTOS = new ArrayList<T>();
//        for (T item : items) {
//            Long dbUserId;
//            if (tClass.equals(QuestionDTO.class)) {
//                dbUserId = ((Question) item).getCreator();
//            } else if (tClass.equals(ArticleDTO.class)) {
//                dbUserId = ((Article) item).getCreator();
//            } else if (tClass.equals(DraftDTO.class)) {
//                dbUserId = ((Draft) item).getCreator();
//            } else {
//                dbUserId = 0L;
//            }
//            User user = userMap.get(dbUserId);
//
//            if (user == null) {
//                // 数据库会用外键联系用户和问题创建人，所有是不会存在找不到用户的情况的，但以防万一先抛个异常
//                throw new CustomizeException(CustomizeErrorCode.SYSTEM_ERROR);
//            }
//
//            if (tClass.equals(QuestionDTO.class)) {
//                QuestionDTO questionDTO = new QuestionDTO();
//                BeanUtils.copyProperties(item, questionDTO);
//                questionDTO.setUser(user);
//                itemDTOS.add((T) questionDTO);
//            } else if (tClass.equals(ArticleDTO.class)) {
//                ArticleDTO articleDTO = new ArticleDTO();
//                BeanUtils.copyProperties(item, articleDTO);
//                articleDTO.setUser(user);
//                itemDTOS.add((T) articleDTO);
//            } else if (tClass.equals(DraftDTO.class)) {
//                DraftDTO draftDTO = new DraftDTO();
//                BeanUtils.copyProperties(item, draftDTO);
//                draftDTO.setUser(user);
//                itemDTOS.add((T) draftDTO);
//            } else {
//
//            }
//
//        }
//        paginationDTO.setData(itemDTOS);
//        return paginationDTO;
//    }
//}
