package com.example.blognpc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blognpc.dto.ArticleDTO;
import com.example.blognpc.dto.PaginationDTO;
import com.example.blognpc.dto.QuestionDTO;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.mapper.QuestionExtMapper;
import com.example.blognpc.mapper.QuestionMapper;
import com.example.blognpc.mapper.UserMapper;
import com.example.blognpc.model.Question;
import com.example.blognpc.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;

    public void createOrUpdate(Question question) {
        if (question.getId() == null) {
            // 创建问题
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setCommentCount(0);
            question.setViewCount(0);
            question.setLikeCount(0);
            questionMapper.insert(question);
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

    public PaginationDTO<QuestionDTO> list(Long id, Long page, Long size) {
        Long totalCount = questionMapper.selectCount(new QueryWrapper<Question>().eq(id != 0L, "creator", id));
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setPagination(totalCount, page, size);
        page = paginationDTO.getPage();

        Long offset = (page - 1) * size;
        List<Question> questions = questionMapper.selectList(new QueryWrapper<Question>()
                        .eq(id != 0L, "creator", id)
                .orderByDesc("id")
                .last(String.format("limit %d, %d", offset, size)));
        List<QuestionDTO> questionDTOS = new ArrayList<QuestionDTO>();
        for (Question question : questions) {
            List<User> users = userMapper.selectList(new QueryWrapper<User>().eq("id", question.getCreator()));
            User user = users.size() == 0 ? null : users.get(0);
            if (user == null) {
                // 数据库会用外键联系用户和问题创建人，所有是不会存在找不到用户的情况的，但以防万一先抛个异常
                throw new CustomizeException(CustomizeErrorCode.SYSTEM_ERROR);
            } else {
                QuestionDTO questionDTO = new QuestionDTO();
                BeanUtils.copyProperties(question, questionDTO);
                questionDTO.setUser(user);
                questionDTOS.add(questionDTO);
            }
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
        List<Question> questions = questionExtMapper.selectRegexp("tag", regexp, size);
        List<QuestionDTO> questionDTOS = questions.stream().filter(question -> !(question.getId() == id)).map(question -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            // 这里并没有把 user信息 放到questionDTO之中，注意一下
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOS;
    }
}
