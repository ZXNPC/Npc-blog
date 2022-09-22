package com.example.blognpc.dto;

import lombok.Data;

@Data
public class DraftCreateDTO {
    private Long id;
    private String title;
    private String description;
    private String tag;
    private Integer type;
}
