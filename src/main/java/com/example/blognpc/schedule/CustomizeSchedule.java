package com.example.blognpc.schedule;

import com.example.blognpc.mapper.UserMapper;
import com.example.blognpc.service.UserService;
import com.example.blognpc.service.UserUnverifiedService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;

@Component
@Slf4j
public class CustomizeSchedule {
    @Autowired
    private UserUnverifiedService userUnverifiedService;
    @Scheduled(fixedRate = 86400000)
    public void userWaitingListSchedule() {
        userUnverifiedService.removeExpired();
        log.info("清理未验证的过期用户 " + new Date());
    }
}
