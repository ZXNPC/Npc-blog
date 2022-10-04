package com.example.blognpc.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blognpc.enums.NotificationStatusEnum;
import com.example.blognpc.mapper.ManagerMapper;
import com.example.blognpc.mapper.NotificationMapper;
import com.example.blognpc.mapper.UserMapper;
import com.example.blognpc.model.Manager;
import com.example.blognpc.model.Notification;
import com.example.blognpc.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class SessionInterceptor implements HandlerInterceptor {
    @Value("${github.client.id}")
    private String clientId;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private ManagerMapper managerMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    List<User> users = userMapper.selectList(new QueryWrapper<User>().eq("token", token));
                    if (users.size() != 0) {
                        User user = users.get(0);
                        request.getSession().setAttribute("user", user);
                        List<Manager> managers = managerMapper.selectList(new QueryWrapper<Manager>().eq("user_id", user.getId()));
                        request.getSession().setAttribute("manager", managers.size() != 0);
                        Long unreadCount = notificationMapper.selectCount(new QueryWrapper<Notification>()
                                .eq("receiver", user.getId())
                                .eq("status", NotificationStatusEnum.UNREAD));
                        request.getSession().setAttribute("unreadCount", unreadCount);
                    }
                }
            }
        }
        request.getSession().setAttribute("clientId", clientId);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
