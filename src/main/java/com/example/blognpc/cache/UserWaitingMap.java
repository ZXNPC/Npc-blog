package com.example.blognpc.cache;

import com.example.blognpc.model.UserWaiting;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UserWaitingMap {
    private static Map<String, UserWaiting> userWaitingMap = new HashMap<>();

    public static void add(String token, UserWaiting userWaiting) {
        userWaitingMap.put(token, userWaiting);
    }

    public static UserWaiting get(String token) { return userWaitingMap.get(token); }

    public static void removeExpiration() {
        Long currentTimeMillis = System.currentTimeMillis();
        for (String key : userWaitingMap.keySet()) {
            if (userWaitingMap.get(key).getExpirationTime() >= currentTimeMillis) {
                userWaitingMap.remove(key);
                log.info(userWaitingMap.get(key) + "has reached expiration. Remove from userWaitingMap");
            }
        }
    }
}
