package com.example.blognpc.enums;

import org.apache.commons.lang3.StringUtils;

public enum NotificationTypeEnum {
    REPLY_COMMUNITY_QUESTION(1, "回复了问题"),
    REPLY_COMMUNITY_COMMENT(2, "回复了问题评论"),
    REPLY_MUMBLER_ARTICLE(3, "回复了文章"),
    REPLY_MUMBLER_COMMENT(4, "回复了文章评论");


    private Integer type;
    private String name;

    NotificationTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static String nameOfType(Integer type) {
        String typeName = getByType(type).getName();
        if (StringUtils.isBlank(typeName))
            return "";
        return typeName;
    }

    public static NotificationTypeEnum getByType(Integer type) {
        for (NotificationTypeEnum notificationTypeEnum : NotificationTypeEnum.values()) {
            if (notificationTypeEnum.getType() == type) {
                return notificationTypeEnum;
            }
        }
        return null;
    }


    public static NotificationTypeEnum toNotificationTypeEnum(CommentTypeEnum commentTypeEnum) {
        return getByType(commentTypeEnum.getType()); // 一一对应
    }
}
