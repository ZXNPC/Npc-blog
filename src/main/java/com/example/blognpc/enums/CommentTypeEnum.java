package com.example.blognpc.enums;

public enum CommentTypeEnum {
    COMMUNITY_QUESTION(1),
    COMMUNITY_COMMENT(2),
    MUMBLER_ARTICLE(3),
    MUMBLER_COMMENT(4);

    private Integer type;

    CommentTypeEnum(Integer type) {
        this.type = type;
    }

    public static boolean isExist(Integer type) {
        for (CommentTypeEnum commentTypeEnum : CommentTypeEnum.values()) {
            if (commentTypeEnum.getType() == type) {
                return true;
            }
        }
        return false;
    }

    public Integer getType() {
        return type;
    }
}
