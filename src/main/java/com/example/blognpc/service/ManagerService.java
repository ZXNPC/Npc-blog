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
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ManagerMapper managerMapper;

    public Boolean isManager(User user) {
        return isManager(user.getId());
    }

    public Boolean isManager(Long id) {
        List<Manager> managers = managerMapper.selectList(new QueryWrapper<Manager>().eq("user_id", id));
        if (managers.size() == 0) {
            return false;
        }
        return true;
    }
}
