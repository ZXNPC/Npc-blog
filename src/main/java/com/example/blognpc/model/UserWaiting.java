package com.example.blognpc.model;

import lombok.Data;

@Data
public class UserWaiting {
    String name;
    String email;
    Long expirationTime;
}
