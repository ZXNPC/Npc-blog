package com.example.blognpc.exception;

import com.example.blognpc.exception.ICustomizeErrorCode;
import lombok.Data;

public class CustomizeErrorException extends RuntimeException {
    private Integer code;
    private String message;

    public CustomizeErrorException(ICustomizeErrorCode errorCode) {
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
