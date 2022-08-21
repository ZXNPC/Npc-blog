package com.example.blognpc.enums;

import com.example.blognpc.exception.ICustomizeErrorCode;

public enum CustomizeErrorCode implements ICustomizeErrorCode {
    QUESTION_NOT_FOUND(2001, "问题不存在，换一个试试？"),
    TARGET_PARENT_NOT_FOUND(2002, "未选中任何问题或评论进行回复"),
    NO_LOGIN(2003, "当前操作需要登录，请登录后重试"),
    SYSTEM_ERROR(2004, "服务器出错力"),
    TYPE_PARAM_WRONG(2005, "评论类型错误或不存在"),
    COMMENT_NOT_FOUND(2006, "回复的评论不存在"),
    ACCOUNT_ERROR(2007, "当前登录用户错误"),
    CONTENT_IS_EMPTY(2008, "评论为空"),
    READ_NOTIFICATION_FAIL(2009, "提示出错"),
    NOTIFICATION_NOT_FOUND(2010, "提示不存在"),
    FILE_UPLOAD_FAIL(2011, "文件上传错误"),
    FRONT_ERROR(2012, "前端怎么做的？？？"),
    TAG_ERROR(2013, "标签错误")
    ;

    @Override
    public String getMessage() {
        return message;
    }
    @Override
    public Integer getCode() {return code; }

    private String message;
    private Integer code;

    CustomizeErrorCode(Integer code, String message) {
        this.message = message;
        this.code = code;
    }
}
