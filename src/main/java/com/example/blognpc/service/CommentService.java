package com.example.blognpc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blognpc.dto.CommentDTO;
import com.example.blognpc.enums.CommentTypeEnum;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.mapper.*;
import com.example.blognpc.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private CommentExtMapper commentExtMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleExtMapper articleExtMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationService notificationService;

    @Transactional
    public void insert(Comment comment) {
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARENT_NOT_FOUND);
        }

        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }

        comment.setLikeCount(0L);
        comment.setCommentCount(0L);
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(comment.getGmtCreate());

        if (comment.getType() == CommentTypeEnum.COMMUNITY_QUESTION.getType()) {
            // 回复社区问题
            Question dbQuestion = questionMapper.selectById(comment.getParentId());
            if (dbQuestion == null) {
                // 回复的问题不存在
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

            commentMapper.insert(comment);
            questionExtMapper.incComment(dbQuestion.getId());
            notificationService.create(comment.getCommentator(), dbQuestion.getCreator(), comment.getParentId(),
                    CommentTypeEnum.getByType(comment.getType()), dbQuestion.getTitle());
        } else if (comment.getType() == CommentTypeEnum.COMMUNITY_COMMENT.getType()) {
            // 回复社区评论
            Comment dbComment = commentMapper.selectById(comment.getParentId());
            if (dbComment == null) {
                // 回复的评论不存在
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }

            Question dbQuestion = questionMapper.selectById(dbComment.getParentId());
            if (dbQuestion == null) {
                // 回复的问题不存在
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

            commentMapper.insert(comment);
            commentExtMapper.incComment(dbComment.getId());
            // 回复问题创建人和评论创建人
            notificationService.create(comment.getCommentator(), dbComment.getCommentator(), comment.getParentId(),
                    CommentTypeEnum.getByType(comment.getType()), dbQuestion.getTitle());
            notificationService.create(comment.getCommentator(), dbQuestion.getCreator(), comment.getParentId(),
                    CommentTypeEnum.getByType(comment.getType()), dbQuestion.getTitle());
        } else if (comment.getType() == CommentTypeEnum.MUMBLER_ARTICLE.getType()) {
            // 回复文章
            Article dbArticle = articleMapper.selectById(comment.getParentId());
            if (dbArticle == null) {
                // 回复的文章不存在
                throw new CustomizeException(CustomizeErrorCode.ARTICLE_NOT_FOUND);
            }

            commentMapper.insert(comment);
            articleExtMapper.incComment(dbArticle.getId());
            notificationService.create(comment.getCommentator(), dbArticle.getCreator(), comment.getParentId(),
                    CommentTypeEnum.getByType(comment.getType()), dbArticle.getTitle());
        } else if (comment.getType() == CommentTypeEnum.MUMBLER_COMMENT.getType()) {
            // 回复文章评论
            Comment dbComment = commentMapper.selectById(comment.getParentId());
            if (dbComment == null) {
                // 回复的评论不存在
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }

            Article dbArticle = articleMapper.selectById(dbComment.getParentId());
            if (dbArticle == null) {
                // 回复的文章不存在
                throw new CustomizeException(CustomizeErrorCode.ARTICLE_NOT_FOUND);
            }

            commentMapper.insert(comment);
            commentExtMapper.incComment(dbComment.getId());

            // 同时回复文章作者和评论创建人
            notificationService.create(comment.getCommentator(), dbComment.getCommentator(), comment.getParentId(),
                    CommentTypeEnum.getByType(comment.getType()), dbArticle.getTitle());
            notificationService.create(comment.getCommentator(), dbArticle.getCreator(), comment.getParentId(),
                    CommentTypeEnum.getByType(comment.getType()), dbArticle.getTitle());
        } else {
        }

        return;
    }

    public List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<Comment>();
        queryWrapper.eq("parent_id", id).eq("type", type.getType());
        queryWrapper.orderByDesc("gmt_create");
        List<Comment> comments = commentMapper.selectList(queryWrapper);
        if (comments.size() == 0) {
            return new ArrayList<>();
        }

        // 获取评论人并去重
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList<>();
        userIds.addAll(commentators);

        // 获取评论人id并转换为map
        List<User> users = userMapper.selectList(new QueryWrapper<User>().in("id", userIds));
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        // 转换commet为commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(commentDTO.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());

        return commentDTOS;
    }
}
