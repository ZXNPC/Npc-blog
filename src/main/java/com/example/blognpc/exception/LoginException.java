package com.example.blognpc.exception;

public class LoginException extends RuntimeException{
    private Integer code;
    private String message;

    public LoginException(ICustomizeErrorCode errorCode) {
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

    public Boolean is1xxSigninError() {
        if (code >= 100 && code < 200) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean is2xxSignupError() {
        if (code >= 200 && code < 300) {
            return true;
        } else {
            return false;
        }
    }
}
