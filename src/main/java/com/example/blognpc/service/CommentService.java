package com.example.blognpc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.blognpc.dto.CommentDTO;
import com.example.blognpc.enums.CommentTypeEnum;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.enums.NotificationTypeEnum;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.mapper.*;
import com.example.blognpc.model.*;
import org.checkerframework.checker.units.qual.C;
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
    private QuestionMapper questionMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private ArticleService articleService;

    public void incComment(Long id) {
        UpdateWrapper<Comment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.setSql("comment_count = comment_count + 1");
        commentMapper.update(null, updateWrapper);
    }

    @Transactional
    public void insert(Comment comment) {
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARENT_NOT_FOUND);
        }

        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            throw new CustomizeException(CustomizeErrorCode.COMMENT_TYPE_WRONG);
        }

        comment.setLikeCount(0L);
        comment.setCommentCount(0L);
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(comment.getGmtCreate());

        if (comment.getType() == CommentTypeEnum.COMMUNITY_QUESTION.getType()) {
            // ????????????
            Question dbQuestion = questionMapper.selectById(comment.getParentId());
            if (dbQuestion == null) {
                // ????????????????????????
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

            commentMapper.insert(comment);
            questionService.incCommet(dbQuestion.getId());

            // ?????????????????????
            notificationService.create(comment.getCommentator(), dbQuestion.getCreator(), comment.getId(), dbQuestion.getId(),
                    NotificationTypeEnum.REPLY_COMMUNITY_QUESTION.getType());
        } else if (comment.getType() == CommentTypeEnum.COMMUNITY_COMMENT.getType()) {
            // ??????????????????
            Comment dbComment = commentMapper.selectById(comment.getParentId());
            if (dbComment == null) {
                // ????????????????????????
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }

            Question dbQuestion = questionMapper.selectById(dbComment.getParentId());
            if (dbQuestion == null) {
                // ????????????????????????
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

            commentMapper.insert(comment);
            incComment(dbComment.getId());

            // ?????????????????????
            notificationService.create(comment.getCommentator(), dbComment.getCommentator(), comment.getId(), dbComment.getId(),
                    NotificationTypeEnum.REPLY_COMMUNITY_COMMENT.getType());
            // ?????????????????????
            if (dbQuestion.getCreator() != dbComment.getCommentator()) {
                notificationService.create(comment.getCommentator(), dbQuestion.getCreator(), comment.getId(), dbQuestion.getId(),
                        NotificationTypeEnum.REPLY_COMMUNITY_QUESTION.getType());
            } else {
                // ?????????????????????????????????????????????????????????????????????
            }
        } else if (comment.getType() == CommentTypeEnum.MUMBLER_ARTICLE.getType()) {
            // ????????????
            Article dbArticle = articleMapper.selectById(comment.getParentId());
            if (dbArticle == null) {
                // ????????????????????????
                throw new CustomizeException(CustomizeErrorCode.ARTICLE_NOT_FOUND);
            }

            commentMapper.insert(comment);
            articleService.incCommet(dbArticle.getId());

            // ?????????????????????
            notificationService.create(comment.getCommentator(), dbArticle.getCreator(), comment.getId(), dbArticle.getId(),
                    NotificationTypeEnum.REPLY_MUMBLER_ARTICLE.getType());
        } else if (comment.getType() == CommentTypeEnum.MUMBLER_COMMENT.getType()) {
            // ??????????????????
            Comment dbComment = commentMapper.selectById(comment.getParentId());
            if (dbComment == null) {
                // ????????????????????????
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }

            Article dbArticle = articleMapper.selectById(dbComment.getParentId());
            if (dbArticle == null) {
                // ????????????????????????
                throw new CustomizeException(CustomizeErrorCode.ARTICLE_NOT_FOUND);
            }

            commentMapper.insert(comment);
            incComment(dbComment.getId());

            // ?????????????????????
            notificationService.create(comment.getCommentator(), dbComment.getCommentator(), comment.getId(), dbComment.getId(),
                    NotificationTypeEnum.REPLY_MUMBLER_COMMENT.getType());
            // ?????????????????????
            if (dbArticle.getCreator() != dbComment.getCommentator()) {
                notificationService.create(comment.getCommentator(), dbArticle.getCreator(), comment.getId(), dbArticle.getId(),
                        NotificationTypeEnum.REPLY_MUMBLER_ARTICLE.getType());
            } else {
                // ?????????????????????????????????????????????????????????????????????
            }
        } else {
            // ???????????????????????????
        }

    }

    public List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<Comment>();
        queryWrapper.eq("parent_id", id).eq("type", type.getType());
        queryWrapper.orderByDesc("gmt_create");
        List<Comment> comments = commentMapper.selectList(queryWrapper);
        if (comments.size() == 0) {
            return new ArrayList<>();
        }

        // ????????????????????????
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList<>();
        userIds.addAll(commentators);

        // ???????????????id????????????map
        List<User> users = userMapper.selectList(new QueryWrapper<User>().in("id", userIds));
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        // ??????commet???commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(commentDTO.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());

        return commentDTOS;
    }

    public void deleteByParentId(Long parentId, Integer type) {
        // ???????????? parent_id ??? type ??????????????? id
        List<Long> commentIds = commentMapper.selectList(new QueryWrapper<Comment>().eq("parent_id", parentId).eq("type", type)).stream().map(comment -> comment.getId()).collect(Collectors.toList());
        if (commentIds.size() == 0) commentIds.add(0L);

        // ?????? type ?????? childType
        Integer childType;
        if (type == CommentTypeEnum.COMMUNITY_QUESTION.getType()) {
            childType = CommentTypeEnum.COMMUNITY_COMMENT.getType();
        } else if (type == CommentTypeEnum.MUMBLER_ARTICLE.getType()) {
            childType = CommentTypeEnum.MUMBLER_COMMENT.getType();
        } else {
            childType = null;
        }

        // ??????????????? id
        if (childType != null) {
            List<Long> commentChildIds = commentMapper.selectList(new QueryWrapper<Comment>().in("parent_id", commentIds).eq("type", childType)).stream().map(comment -> comment.getId()).collect(Collectors.toList());
            commentIds.addAll(commentChildIds);
        }

        // ????????????????????????
        commentMapper.deleteBatchIds(commentIds);
    }
}
