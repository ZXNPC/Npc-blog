package com.example.blognpc.dto;

import com.example.blognpc.model.User;
import lombok.Data;

@Data
public class DraftDTO {
    private Long id;
    private Integer type;
    private String title;
    private String description;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;
    private Long creator;
    private Long outerId;
    private User user;
}
