package com.example.blognpc.schedule;

import com.example.blognpc.mapper.UserMapper;
import com.example.blognpc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CustomizeSchedule {
    @Autowired
    private UserService userService;
    @Scheduled(fixedRate = 86400000)
    public void userWaitingListSchedule() {
        userService.removeExpired();
    }
}
