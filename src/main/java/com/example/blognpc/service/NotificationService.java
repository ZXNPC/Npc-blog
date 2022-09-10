package com.example.blognpc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blognpc.dto.NotificationDTO;
import com.example.blognpc.dto.PaginationDTO;
import com.example.blognpc.dto.ResultDTO;
import com.example.blognpc.enums.CommentTypeEnum;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.enums.NotificationStatusEnum;
import com.example.blognpc.enums.NotificationTypeEnum;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.mapper.*;
import com.example.blognpc.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private ArticleMapper articleMapper;

    public PaginationDTO<NotificationDTO> list(Long receiverId, Long page, Long size) {
        Long totalCount = notificationMapper.selectCount(new QueryWrapper<Notification>().eq(receiverId != 0L, "receiver", receiverId));
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setPagination(totalCount, page, size);
        page = paginationDTO.getPage();

        Long offset = (page - 1) * size;
        List<Notification> notifications = notificationMapper.selectList(new QueryWrapper<Notification>()
                .eq(receiverId != 0L, "receiver", receiverId)
                        .orderByAsc("status")
                .orderByDesc("gmt_modified")
                .last(String.format("limit %d, %d", offset, size)));
        List<NotificationDTO> notificationDTOS = new ArrayList<NotificationDTO>();
        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = convertToDTO(notification);
            notificationDTOS.add(notificationDTO);
        }
        paginationDTO.setData(notificationDTOS);
        return paginationDTO;
    }

    public void create(Long notifierId, Long receiverId, Long commentId, Long outerId, Integer type) {
        if (notifierId == receiverId) {
            // 自己回复自己
            return;
        }
        Notification notification = new Notification();
        notification.setNotifier(notifierId);
        notification.setReceiver(receiverId);
        notification.setCommentId(commentId);
        notification.setOuterId(outerId);
        notification.setType(type);
        notification.setStatus(NotificationStatusEnum.UNREAD.getType());
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setGmtModified(notification.getGmtCreate());
        notificationMapper.insert(notification);
        return;
    }

    public NotificationDTO read(Long id, User receiverUser) {
        Notification dbNotification = notificationMapper.selectById(id);
        if (dbNotification == null) {
            // 回复不存在
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if (dbNotification.getReceiver() != receiverUser.getId()) {
            // 用户信息错误
            throw new CustomizeException(CustomizeErrorCode.ACCOUNT_ERROR);
        }

        dbNotification.setGmtModified(System.currentTimeMillis());
        dbNotification.setStatus(NotificationStatusEnum.READ.getType());
        notificationMapper.updateById(dbNotification);

        return convertToDTO(dbNotification);
    }

    public NotificationDTO convertToDTO(Notification notification) {
        User notifierUser = userMapper.selectById(notification.getNotifier());
        if (notifierUser == null) {
            // 回复消息的用户不存在
            throw new CustomizeException(CustomizeErrorCode.USER_NOT_FOUND);
        }

        Comment comment = commentMapper.selectById(notification.getCommentId());

        if (notification.getType() == NotificationTypeEnum.REPLY_MUMBLER_COMMENT.getType()
                || notification.getType() == NotificationTypeEnum.REPLY_COMMUNITY_COMMENT.getType()) {
            // 回复的是评论
            Comment dbComment = commentMapper.selectById(notification.getOuterId());
            if (dbComment == null) {
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }

            if (notification.getType() == NotificationTypeEnum.REPLY_MUMBLER_COMMENT.getType()
                    && dbComment.getType() == CommentTypeEnum.MUMBLER_ARTICLE.getType()) {
                // 回复的是文章的评论
                Article dbArticle = articleMapper.selectById(dbComment.getParentId());
                if (dbArticle == null) {
                    throw new CustomizeException(CustomizeErrorCode.ARTICLE_NOT_FOUND);
                }

                NotificationDTO<Article> notificationDTO = new NotificationDTO<Article>();
                BeanUtils.copyProperties(notification, notificationDTO);
                notificationDTO.setNotifierUser(notifierUser);
                notificationDTO.setOutMostId(dbArticle.getId());
                notificationDTO.setComment(comment);
                notificationDTO.setOuterComment(dbComment);
                notificationDTO.setOuter(dbArticle);

                return notificationDTO;

            } else if (notification.getType() == NotificationTypeEnum.REPLY_COMMUNITY_COMMENT.getType()
                    && dbComment.getType() == CommentTypeEnum.COMMUNITY_QUESTION.getType()) {
                // 回复的是问题的评论
                Question dbQuestion = questionMapper.selectById(dbComment.getParentId());
                if (dbQuestion == null) {
                    throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
                }

                NotificationDTO<Question> notificationDTO = new NotificationDTO<Question>();
                BeanUtils.copyProperties(notification, notificationDTO);
                notificationDTO.setNotifierUser(notifierUser);
                notificationDTO.setOutMostId(dbQuestion.getId());
                notificationDTO.setComment(comment);
                notificationDTO.setOuterComment(dbComment);
                notificationDTO.setOuter(dbQuestion);

                return notificationDTO;

            } else {
                // 总之就是有问题，抛异常就完事了
                throw new CustomizeException(CustomizeErrorCode.SYSTEM_ERROR);
            }
        } else {
            // 回复的是文章或问题
            if (notification.getType() == NotificationTypeEnum.REPLY_MUMBLER_ARTICLE.getType()) {
                // 回复的是文章
                Article dbArticle = articleMapper.selectById(notification.getOuterId());
                if (dbArticle == null) {
                    throw new CustomizeException(CustomizeErrorCode.ARTICLE_NOT_FOUND);
                }

                NotificationDTO<Article> notificationDTO = new NotificationDTO<Article>();
                BeanUtils.copyProperties(notification, notificationDTO);
                notificationDTO.setNotifierUser(notifierUser);
                notificationDTO.setOutMostId(dbArticle.getId());
                notificationDTO.setComment(comment);
                notificationDTO.setOuterComment(null);
                notificationDTO.setOuter(dbArticle);

                return notificationDTO;

            } else if (notification.getType() == NotificationTypeEnum.REPLY_COMMUNITY_QUESTION.getType()) {
                // 回复的是问题
                Question dbQuestion = questionMapper.selectById(notification.getOuterId());
                if (dbQuestion == null) {
                    throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
                }

                NotificationDTO<Question> notificationDTO = new NotificationDTO<Question>();
                BeanUtils.copyProperties(notification, notificationDTO);
                notificationDTO.setNotifierUser(notifierUser);
                notificationDTO.setOutMostId(dbQuestion.getId());
                notificationDTO.setComment(comment);
                notificationDTO.setOuterComment(null);
                notificationDTO.setOuter(dbQuestion);

                return notificationDTO;

            } else {
                // 总之就是有问题，抛异常就完事了
                throw new CustomizeException(CustomizeErrorCode.SYSTEM_ERROR);
            }
        }

    }

    public ResultDTO delete(Long id, User user) {
        Notification dbNotification = notificationMapper.selectById(id);
        if (dbNotification == null) {
            return ResultDTO.errorOf(new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND));
        }

        if (dbNotification.getReceiver() != user.getId()) {
            return ResultDTO.errorOf(new CustomizeException(CustomizeErrorCode.ACCOUNT_ERROR));
        }

        notificationMapper.deleteById(dbNotification);
        return ResultDTO.okOf();
    }
}
