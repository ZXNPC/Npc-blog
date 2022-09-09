package com.example.blognpc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blognpc.dto.ArticleDTO;
import com.example.blognpc.dto.NotificationDTO;
import com.example.blognpc.dto.PaginationDTO;
import com.example.blognpc.dto.QuestionDTO;
import com.example.blognpc.enums.CommentTypeEnum;
import com.example.blognpc.enums.CustomizeErrorCode;
import com.example.blognpc.enums.NotificationStatusEnum;
import com.example.blognpc.enums.NotificationTypeEnum;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.mapper.NotificationMapper;
import com.example.blognpc.mapper.UserMapper;
import com.example.blognpc.model.Article;
import com.example.blognpc.model.Notification;
import com.example.blognpc.model.Question;
import com.example.blognpc.model.User;
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

    public PaginationDTO<NotificationDTO> list(Long id, Long page, Long size) {
        Long totalCount = notificationMapper.selectCount(new QueryWrapper<Notification>().eq(id != 0L, "receiver", id));
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setPagination(totalCount, page, size);
        page = paginationDTO.getPage();

        Long offset = (page - 1) * size;
        List<Notification> notifications = notificationMapper.selectList(new QueryWrapper<Notification>()
                .eq(id != 0L, "receiver", id)
                .orderByDesc("id")
                .last(String.format("limit %d, %d", offset, size)));
        List<NotificationDTO> notificationDTOS = new ArrayList<NotificationDTO>();
        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification, notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notificationDTO.getType()));
            notificationDTOS.add(notificationDTO);
        }
        paginationDTO.setData(notificationDTOS);
        return paginationDTO;
    }

    public void create(Long notifierId, Long receiver, Long outerId, CommentTypeEnum commentTypeEnum, String outerTitle) {
        Notification notification = new Notification();
        notification.setNotifier(notifierId);
        User user = userMapper.selectById(notifierId);
        if (user == null) throw new CustomizeException(CustomizeErrorCode.USER_NOT_FOUND);
        notification.setNotifierName(user.getName());
        notification.setReceiver(receiver);
        notification.setOuterId(outerId);
        notification.setType(NotificationTypeEnum.toNotificationTypeEnum(commentTypeEnum).getType());
        notification.setOuterTitle(outerTitle);
        notification.setStatus(NotificationStatusEnum.UNREAD.getType());
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setGmtModified(notification.getGmtCreate());
        notificationMapper.insert(notification);
    }

    public NotificationDTO read(Long id, User user) {
        Notification dbNotification = notificationMapper.selectById(id);
        if (dbNotification == null) {
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if (dbNotification.getReceiver() != user.getId()) {
            throw new CustomizeException(CustomizeErrorCode.ACCOUNT_ERROR);
        }
        dbNotification.setStatus(NotificationStatusEnum.READ.getType());
        notificationMapper.updateById(dbNotification);

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(dbNotification, notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notificationDTO.getType()));

        return notificationDTO;
    }
}
