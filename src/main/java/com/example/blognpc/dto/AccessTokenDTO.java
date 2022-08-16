package com.example.blognpc.dto;

import lombok.Data;

@Data
public class AccessTokenDTO {
    String clientId;
    String clientSecret;
    String code;
    String redirectUri;
    String state;
}
