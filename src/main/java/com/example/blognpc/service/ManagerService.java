package com.example.blognpc.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blognpc.mapper.ManagerMapper;
import com.example.blognpc.mapper.UserMapper;
import com.example.blognpc.model.Manager;
import com.example.blognpc.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class ManagerService {
    private static ManagerService managerService;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ManagerMapper managerMapper;

    @PostConstruct
    public void init() {
        managerService = this;
        managerService.managerMapper = this.managerMapper;
    }

    public static Boolean isManager(User user) {
        List<Manager> managers = managerService.managerMapper.selectList(new QueryWrapper<Manager>().eq("user_id", user.getId()));
        if (managers.size() == 0) {
            return false;
        }
        return true;
    }

    public static Boolean isManager(Long id) {
        List<Manager> managers = managerService.managerMapper.selectList(new QueryWrapper<Manager>().eq("user_id", id));
        if (managers.size() == 0) {
            return false;
        }
        return true;
    }
}
