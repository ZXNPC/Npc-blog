package com.example.blognpc.enums;

import com.example.blognpc.exception.ICustomizeErrorCode;

public enum LoginErrorCode implements ICustomizeErrorCode {
    EMAIL_NOT_FOUND(1, "邮箱错误或未注册"),
    PASSWORD_NOT_CORRECT(2, "邮箱正确，但是密码错误"),
    EMAIL_FOUND(3, "邮箱已注册，请直接登录")
    ;

    Integer code;
    String message;

    LoginErrorCode(Integer code, String message) {
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
