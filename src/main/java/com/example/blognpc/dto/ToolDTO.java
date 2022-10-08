package com.example.blognpc.dto;

import com.example.blognpc.model.User;
import lombok.Data;

@Data
public class ToolDTO {
    private Long id;
    private String title;
    private String url;
    private String tag;
    private Integer viewCount;
    private Long creator;
    private Integer likeCount;
    private Long gmtCreate;
    private Long gmtModified;
    private User user;
}
