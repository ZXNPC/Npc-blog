package com.example.blognpc.enums;

public enum DraftTypeEnum {
    QUESTION_DRAFT(0),
    ARTICLE_DRAFT(1),
    TOOL_DRAFT(2)
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
