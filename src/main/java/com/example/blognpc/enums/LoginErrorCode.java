package com.example.blognpc.enums;

import com.example.blognpc.exception.ICustomizeErrorCode;

public enum LoginErrorCode implements ICustomizeErrorCode {
    EMAIL_NOT_FOUND(1, "邮箱错误或未注册"),
    PASSWORD_NOT_CORRECT(2, "邮箱正确，但是密码错误"),
    EMAIL_FOUND(3, "邮箱已注册，请直接登录"),
    EMAIL_UNVERIFIED(4, "邮箱未验证，请注意查看邮箱链接"),
    TOKEN_NOT_FOUND(5, "个人令牌错误，过期或者未注册"),
    TOKEN_REACH_EXPIRATION(6, "个人令牌已过期，尝试重新注册"),
    EXPIRATION_UNCORRECT(7, "过期时间不匹配，请不要随意修改链接"),
    EMAIL_ALREADY_VERIFIED(8, "邮箱已验证，不要再点这个链接了啊喂"),
    GITHUB_OAUTH_ERROR(9, "GitHub日常抽风，请再试一次或者使用邮箱注册及登录"),
    EMAIL_SEND_EXCEPTION(10, "验证邮件发送失败，请检查邮箱是否正确并重试")
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
