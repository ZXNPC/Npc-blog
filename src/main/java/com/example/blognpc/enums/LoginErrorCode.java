package com.example.blognpc.enums;

import com.example.blognpc.exception.ICustomizeErrorCode;

public enum LoginErrorCode implements ICustomizeErrorCode {
    EMAIL_NOT_FOUND(101, "邮箱错误或未注册"),
    PASSWORD_NOT_CORRECT(102, "邮箱正确，但是密码错误"),
    EMAIL_FOUND(203, "邮箱已注册，请直接登录"),
    EMAIL_UNVERIFIED_SIGNIN(104, "邮箱未验证，请注意查看邮箱链接"),
    EMAIL_UNVERIFIED_SIGNUP(205, "邮箱未验证，请注意查看邮箱链接"),
    TOKEN_NOT_FOUND(206, "个人令牌错误，过期或者未注册"),
    TOKEN_REACH_EXPIRATION(207, "个人令牌已过期，尝试重新注册"),
    LINK_NOT_CORRECT(208, "用户信息不匹配，请不要随意修改链接"),
    EMAIL_ALREADY_VERIFIED(209, "邮箱已验证，不要再点这个链接了啊喂"),
    GITHUB_OAUTH_ERROR(110, "GitHub日常抽风，请再试一次或者使用邮箱注册及登录"),
    EMAIL_SEND_EXCEPTION(211, "验证邮件发送失败，请检查邮箱是否正确并重试")
    ;

    private Integer code;
    private String message;

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
