package com.example.blognpc.controller;

import com.example.blognpc.cache.UserWaitingMap;
import com.example.blognpc.dto.ResultDTO;
import com.example.blognpc.enums.LoginErrorCode;
import com.example.blognpc.model.User;
import com.example.blognpc.model.UserWaiting;
import com.example.blognpc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.UUID;

@Controller
public class LoginController {
    @Value("${github.client.id}")
    private String clientId;
    @Value("${signup.user.waiting.expiration}")
    private Long expiration;
    @Autowired
    private UserService userService;
    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("clientId", clientId);
        return "login";
    }

    @PostMapping("/login/signin")
    public String signin(@RequestParam(value = "email") String email,
                         @RequestParam(value = "signin_password") String password,
                         HttpServletResponse response,
                         Model model) {
        Object info = userService.passwordLogin(email, password);
        if (info.getClass().equals(User.class)) {
            // 用户认证完成，返回主页
            User user = (User) info;
            response.addCookie(new Cookie("token", user.getToken()));
            return "redirect:/";
        } else if (info.getClass().equals(ResultDTO.class)) {
            // 用户认证失败，返回错误信息
            ResultDTO resultDTO = (ResultDTO) info;
//            ModelAndView modelAndView = new ModelAndView("login");
//            modelAndView.addObject("resultDTO", resultDTO);
//            modelAndView.addObject("signin_email", email);
//            modelAndView.addObject("signin_password", password);
//            return modelAndView;
            model.addAttribute("resultDTO", resultDTO);
            model.addAttribute("email", email);
            model.addAttribute("signin_password", password);
            return "/login";
        }
        return null;
    }

    @PostMapping("/login/signup")
    public String signup(@RequestParam(value = "signup_user") String userName,
                         @RequestParam(value = "email") String email,
                         Model model,
                         HttpServletRequest request) {
        Object info = userService.checkEmail(email);
        if (info != null) {
            // 邮箱已存在，请直接登录
            ResultDTO resultDTO = (ResultDTO) info;
            model.addAttribute("resultDTO", resultDTO);
            return "/login";
        }

        String token = UUID.randomUUID().toString();
        UserWaiting userWaiting = new UserWaiting();
        userWaiting.setName(userName);
        userWaiting.setEmail(email);
        userWaiting.setExpirationTime(System.currentTimeMillis() + expiration);
        UserWaitingMap.add(token, userWaiting);

        String url = String.join("/", Arrays.copyOfRange(request.getRequestURL().toString().split("/"), 0, 3))
                + "/tokenLogin/" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("2946310156@qq.com");
        message.setTo(email);
        message.setSubject("NPC Blog 邮箱验证");
        message.setText("此邮件为系统自动生成，请勿直接回复。\n请点击以下链接验证你的邮箱地址：\n" + url + "\n该链接将于"
                + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(userWaiting.getExpirationTime()) + "过期。\n" +
                "你的个人登录token为：" + token);
        try {
            mailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
            return null;
        }
        model.addAttribute("resultDTO", ResultDTO.okOf(email));
        return "/login";
    }

    @GetMapping("/tokenLogin/{token}")
    public String tokenLogin(@PathVariable(value = "token") String token) {
        UserWaiting userWaiting = UserWaitingMap.get(token);
        User user = userService.saveOrUpdate(token, userWaiting);
    }
}
