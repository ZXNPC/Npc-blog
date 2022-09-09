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

    public Integer getType() {
        return type;
    }

    public static boolean isExist(Integer type) {
        CommentTypeEnum commentTypeEnum = getByType(type);
        if (commentTypeEnum == null) return false;
        return true;
    }

    public static CommentTypeEnum getByType(Integer type) {
        for (CommentTypeEnum commentTypeEnum : CommentTypeEnum.values()) {
            if (commentTypeEnum.getType() == type)
                return commentTypeEnum;
        }
        return null;
    }
}
