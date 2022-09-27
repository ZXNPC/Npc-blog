package com.example.blognpc.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String bio;
    private String avatarUrl;
    private String token;
    private Long gmtCreate;
    private Long gmtModified;
    private Boolean complete;
}
