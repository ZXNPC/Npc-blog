package com.example.blognpc.enums;

public enum DraftTypeEnum {
    QUESTION_DRAFT(0),
    ARTICLE_DRAFT(1),
    TOOL_DRAFT(2),
    ANNO_QUESTION_DRAFT(3),
    ANNO_ARTICLE_DRAFT(4),
    ;

    private Integer type;

    DraftTypeEnum(Integer type) {
        this.type = type;
    }

    public static boolean isExist(Integer type) {
        for (DraftTypeEnum draftTypeEnum : DraftTypeEnum.values()) {
            if (draftTypeEnum.getType() == type)
                return true;
        }
        return false;
    }

    public Integer getType() {
        return type;
    }
}
