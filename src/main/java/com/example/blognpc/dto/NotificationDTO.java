package com.example.blognpc.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private Long notifier;
    private String notifierName;
    private Long receiver;
    private Long outerId;
    private Integer type;
    private String typeName;
    private String outerTitle;
    private Integer status;
    private Long gmtCreate;
    private Long gmtModified;
}
