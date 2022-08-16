package com.example.blognpc.schedule;

import com.example.blognpc.cache.UserWaitingMap;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CustomizeSchedule {

    @Scheduled(fixedRate = 86400000)
    public void userWaitingListSchedule() {
        UserWaitingMap.removeExpiration();
    }
}
