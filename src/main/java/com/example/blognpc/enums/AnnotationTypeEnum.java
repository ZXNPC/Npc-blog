package com.example.blognpc.enums;

public enum AnnotationTypeEnum {
    ARTICLE_ANNO(1),
    QUESTION_ANNO(2)
    ;
    private Integer type;

    AnnotationTypeEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
