package com.example.blognpc.enums;

import com.example.blognpc.exception.ICustomizeErrorCode;

public enum VerifyErrorCode implements ICustomizeErrorCode {
    PASSWORD_ERROR(301, "两次密码输入不一致"),
    TOKEN_REACH_EXPIRATION(207, "个人令牌已过期，尝试重新注册"),
    ;

    private Integer code;
    private String message;

    VerifyErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
