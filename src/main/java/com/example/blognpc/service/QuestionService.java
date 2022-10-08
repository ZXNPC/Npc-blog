package com.example.blognpc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.blognpc.dto.ArticleDTO;
import com.example.blognpc.dto.PaginationDTO;
import com.example.blognpc.dto.QuestionDTO;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.mapper.DraftMapper;
import com.example.blognpc.mapper.QuestionMapper;
import com.example.blognpc.mapper.UserMapper;
import com.example.blognpc.model.Question;
import com.example.blognpc.provider.SearchProvider;
import com.example.blognpc.model.User;
import com.example.blognpc.utils.ServiceUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private DraftMapper draftMapper;
    @Autowired
    private SearchProvider searchProvider;

    public QuestionDTO selectById(Long id) {
        Question question = questionMapper.selectById(id);

        if (question == null)
            // 文章不存在
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);

        User user = userMapper.selectById(question.getCreator());

        if (user == null)
            // 创建文章的用户不存在，当然不应该出现这种情况
            throw new CustomizeException(CustomizeErrorCode.SYSTEM_ERROR);

        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        questionDTO.setUser(user);
        return questionDTO;
    }

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



    public void incView(Long id) {
        UpdateWrapper<Question> updateWrapper = new UpdateWrapper<>();
        updateWrapper.setSql("view_count = view_count+1");
        updateWrapper.eq("id", id);
        questionMapper.update(null, updateWrapper);
    }

    public void incCommet(Long id) {
        UpdateWrapper<Question> updateWrapper = new UpdateWrapper<>();
        updateWrapper.setSql("comment_count = comment_count+1");
        updateWrapper.eq("id", id);
        questionMapper.update(null, updateWrapper);
    }

    public PaginationDTO<QuestionDTO> list(Long page, Long size, String orderDesc) {
        return list(null, page, size, null, orderDesc);
    }

    public PaginationDTO<QuestionDTO> list(Long creator, Long page, Long size, String orderDesc) {
        return list(creator, page, size, null, orderDesc);
    }

    public PaginationDTO<QuestionDTO> list(Long creator, Long page, Long size, String search, String orderDesc) {
        if (StringUtils.isBlank(search)) {
            // 不使用搜索
            Long totalCount = questionMapper.selectCount(null);
            PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();
            paginationDTO.setPagination(totalCount, page, size);
            page = paginationDTO.getPage();

            Long offset = (page - 1) * size;
            List<Question> questions = questionMapper.selectList(new QueryWrapper<Question>()
                    .eq(creator != null && creator != 0, "creator", creator)
                    .orderByDesc(StringUtils.isNotBlank(orderDesc), orderDesc)
                    .last(String.format("limit %d, %d", offset, size)));
            return getQuestionDTOPaginationDTO(paginationDTO, questions);
        } else {
            // 使用搜索
            String regexp = searchProvider.generateRegexp(search, "title");

            QueryWrapper<Question> countWrapper = new QueryWrapper<Question>()
                    .eq(creator != null && creator != 0, "creator", creator)
                    .apply(regexp);

            Long totalCount = null;
            try {
                totalCount = questionMapper.selectCount(countWrapper);
            } catch (BadSqlGrammarException e) {
                return null;
            }
            PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();
            paginationDTO.setPagination(totalCount, page, size);
            page = paginationDTO.getPage();

            Long offset = (page - 1) * size;
            QueryWrapper<Question> selectWrapper = new QueryWrapper<Question>()
                    .eq(creator != null && creator != 0, "creator", creator)
                    .apply(regexp)
                    .orderByDesc(StringUtils.isNotBlank(orderDesc), orderDesc)
                    .last(String.format("limit %d, %d", offset, size));
            List<Question> questions = questionMapper.selectList(selectWrapper);
            return getQuestionDTOPaginationDTO(paginationDTO, questions);
        }
    }

    private PaginationDTO<QuestionDTO> getQuestionDTOPaginationDTO(PaginationDTO<QuestionDTO> paginationDTO, List<Question> questions) {
        Set<Long> creatorList = questions.stream().map(question -> question.getCreator()).collect(Collectors.toSet());
        Map<Long, User> userMap = ServiceUtils.getUserMap(creatorList);

        List<QuestionDTO> questionDTOS = questions.stream().map(question -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(userMap.get(question.getCreator()));
            return questionDTO;
        }).collect(Collectors.toList());
        paginationDTO.setData(questionDTOS);

        return paginationDTO;
    }

    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO, Long size) {
        String tags = queryDTO.getTag();
        String searchContent = Arrays.stream(tags.split(",")).map(tag -> {
            tag = tag.trim();
            return "(" + tag + ",)|(" + tag + "$)";
        }).collect(Collectors.joining("|"));
        String search = String.format("tag:\"%s\"", searchContent);

        return list(null, 0L, size, search, null).getData();
    }

    public List<QuestionDTO> selectRelated(ArticleDTO queryDTO, Long size) {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setTag(queryDTO.getTag());
        return selectRelated(questionDTO, size);
    }
}
