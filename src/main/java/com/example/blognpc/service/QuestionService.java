package com.example.blognpc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.mapper.QuestionMapper;
import com.example.blognpc.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private QuestionMapper questionMapper;

    public void createOrUpdate(Question question) {
        if (question.getId() == null) {
            // 创建问题
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setCommentCount(0);
            question.setViewCount(0);
            question.setLikeCount(0);
            questionMapper.insert(question);
        }
        else {
            // 更新问题
            Question questiondb = questionMapper.selectById(question.getId());
            if (questiondb == null) {
                // 问题不存在
                // TODO: selectById(null) test
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            else {
                // 问题存在
                question.setGmtCreate(questiondb.getGmtCreate());
                question.setGmtModified(System.currentTimeMillis());
                question.setCommentCount(questiondb.getCommentCount());
                question.setViewCount(questiondb.getViewCount());
                question.setLikeCount(questiondb.getLikeCount());
                questionMapper.updateById(question);
            }
        }

    }
}
