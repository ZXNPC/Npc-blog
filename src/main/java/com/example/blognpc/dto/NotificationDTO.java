package com.example.blognpc.dto;

import com.example.blognpc.model.Comment;
import com.example.blognpc.model.User;
import lombok.Data;

@Data
public class NotificationDTO<T> {
    private Long id;
    private User notifierUser;
    private Long receiver;
    private Long outerId;
    private Long outMostId;
    private Integer type;
    private Comment comment;
    private Comment outerComment;
    private T outer;
    private Integer status;
    private Long gmtCreate;
    private Long gmtModified;
}
