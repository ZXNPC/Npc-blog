package com.example.blognpc.enums;

public enum AccountIdEnum {
    DEFAULT_ACCOUNT_ID("0");

    String code;

    AccountIdEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
