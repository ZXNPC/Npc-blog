package com.example.blognpc.exception;

public class VerifyException extends RuntimeException{
    private Integer code;
    private String message;

    public VerifyException(ICustomizeErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

}
