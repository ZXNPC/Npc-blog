package com.example.blognpc.dto;

import com.example.blognpc.model.User;
import lombok.Data;

@Data
public class CommentDTO {
    private Long id;
    private Long parentId;
    private Integer type;
    private String content;
    private Long commentator;
    private Long likeCount;
    private Long commentCount;
    private Long gmtCreate;
    private Long gmtModified;
    private User user;
}
