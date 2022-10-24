package com.example.blognpc.dto;

import com.example.blognpc.model.User;
import lombok.Data;

@Data
public class AnnotationDTO {
    private Long id;
    private Long outerId;
    private String description;
    private Integer type;
    private Long creator;
    private Long gmtCreate;
    private Integer gmtModified;
    private User user;
}
