package com.example.blognpc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blognpc.dto.PaginationDTO;
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

    public PaginationDTO<Question> list(Long page, Long size) {
        Long totalCount = questionMapper.selectCount(null);
        PaginationDTO<Question> paginationDTO = new PaginationDTO<>();
        paginationDTO.setPagination(totalCount, page, size);
        page = paginationDTO.getPage();

        Page<Question> questionPage = questionMapper.selectPage(new Page<Question>(page, size), null);

        return null;
    }
}
