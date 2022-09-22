package com.example.blognpc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.example.blognpc.dto.ArticleDTO;
import com.example.blognpc.dto.PaginationDTO;
import com.example.blognpc.dto.QuestionDTO;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.mapper.DraftMapper;
import com.example.blognpc.mapper.QuestionExtMapper;
import com.example.blognpc.mapper.QuestionMapper;
import com.example.blognpc.mapper.UserMapper;
import com.example.blognpc.model.Question;
import com.example.blognpc.model.User;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.pqc.math.linearalgebra.IntUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;
    @Autowired
    private DraftMapper draftMapper;

    public void createOrUpdate(Question question, Long draftId) {
        if (question.getId() == null) {
            // 创建问题
            // 创建问题的同时如果存在草稿（从草稿页面跳转过来的情况），则删除草稿
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setCommentCount(0);
            question.setViewCount(0);
            question.setLikeCount(0);
            questionMapper.insert(question);
            if (draftId != null)
                draftMapper.deleteById(draftId);
        } else {
            // 更新问题
            Question dbQuestion = questionMapper.selectById(question.getId());
            if (dbQuestion == null) {
                // 问题不存在
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            } else {
                // 问题存在
                question.setGmtCreate(dbQuestion.getGmtCreate());
                question.setGmtModified(System.currentTimeMillis());
                question.setCommentCount(dbQuestion.getCommentCount());
                question.setViewCount(dbQuestion.getViewCount());
                question.setLikeCount(dbQuestion.getLikeCount());
                questionMapper.updateById(question);
            }
        }

    }

    public PaginationDTO<QuestionDTO> list(Long page, Long size) {
        return list(null, page, size, null);
    }

    public PaginationDTO<QuestionDTO> list(Long creator, Long page, Long size) {
        return list(creator, page, size, null);
    }

    public PaginationDTO<QuestionDTO> list(Long creator, Long page, Long size, String search) {
        Long totalCount;
        String titleRegexp;
        if (StringUtils.isBlank(search)) {
            titleRegexp = "";
            totalCount = questionMapper.selectCount(new QueryWrapper<Question>().eq(creator != null && creator != 0L, "creator", creator));
        } else {
            titleRegexp = Arrays.stream(search.split(" ")).filter(s -> StringUtils.isNotBlank(s)).collect(Collectors.joining("|"));
            totalCount = questionExtMapper.selectCountRegexp(null, "title", titleRegexp);
        }
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setPagination(totalCount, page, size);
        page = paginationDTO.getPage();

        Long offset = (page - 1) * size;
        List<Question> questions;
        if (StringUtils.isBlank(titleRegexp)) {
            questions = questionMapper.selectList(new QueryWrapper<Question>()
                    .eq(creator != null && creator != 0L, "creator", creator)
                    .orderByDesc("id")
                    .last(String.format("limit %d, %d", offset, size)));
        } else {
            questions = questionExtMapper.selectRegexp(null, "title", titleRegexp, "gmt_create", 1, offset, size);
        }

        // 生成 key = creator, value = User 的 Map
        List<Long> creatorList = questions.stream().map(question -> question.getCreator()).collect(Collectors.toList());
        // 如果用户数量为空，则添加一个 0 ，防止 sql 语句错误
        if (creatorList.size() == 0) creatorList.add(0L);
        List<User> userList = userMapper.selectList(new QueryWrapper<User>().in("id", creatorList));
        Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        List<QuestionDTO> questionDTOS = new ArrayList<QuestionDTO>();
        for (Question question : questions) {
            User user = userMap.get(question.getCreator());
            if (user == null) {
                // 数据库会用外键联系用户和问题创建人，所有是不会存在找不到用户的情况的，但以防万一先抛个异常
                throw new CustomizeException(CustomizeErrorCode.SYSTEM_ERROR);
            }
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);
        }
        paginationDTO.setData(questionDTOS);
        return paginationDTO;
    }

    public QuestionDTO selectById(Long id) {
        Question question = questionMapper.selectById(id);

        if (question == null)
            // 问题不存在
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);

        User user = userMapper.selectById(question.getCreator());

        if (user == null)
            // 创建问题的用户不存在，当然不应该出现这种情况
            throw new CustomizeException(CustomizeErrorCode.SYSTEM_ERROR);

        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        questionDTO.setUser(user);
        return questionDTO;

    }

    public void incView(Long id) {
        questionExtMapper.incView(id);
    }

    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO, Long size) {
        if (StringUtils.isBlank(queryDTO.getTag())) {
            // 正常前端和数据库都会限制该情况的出现，所以我直接抛异常了
            throw new CustomizeException(CustomizeErrorCode.SYSTEM_ERROR);
        }
        return selectRelated(queryDTO.getId(), queryDTO.getTag(), size);
    }

    public List<QuestionDTO> selectRelated(ArticleDTO queryDTO, Long size) {
        if (StringUtils.isBlank(queryDTO.getTag())) {
            // 正常前端和数据库都会限制该情况的出现，所以我直接抛异常了
            throw new CustomizeException(CustomizeErrorCode.SYSTEM_ERROR);
        }
        return selectRelated(0L, queryDTO.getTag(), size);
    }

    public List<QuestionDTO> selectRelated(Long id, String tags, Long size) {
        // id 是用来过滤问题的，防止相关问题搜寻到自己，而文章的相关问题则不需考虑重复，故设置为0
        String regexp = Arrays.stream(tags.split(",")).map(tag -> {
            tag = tag.trim();
            return "(" + tag + ",)|(" + tag + "$)";
        }).collect(Collectors.joining("|"));
        List<Question> questions = questionExtMapper.selectRegexp(null, "tag", regexp, "gmt_create", 1, 0L, size);
        List<QuestionDTO> questionDTOS = questions.stream().filter(question -> !(question.getId() == id)).map(question -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            // 这里并没有把 user信息 放到questionDTO之中，注意一下
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOS;
    }
}
