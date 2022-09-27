package com.example.blognpc.controller;

import com.example.blognpc.model.User;
import com.example.blognpc.model.UserUnverified;
import com.example.blognpc.service.UserService;
import com.example.blognpc.service.UserUnverifiedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginVerifyController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserUnverifiedService userUnverifiedService;

    @GetMapping("/verify")
    public String verify() {
        return "login-verify";
    }

    @GetMapping("/login/verify")
    public String emailVerify(@RequestParam(value = "token") String token,
                              @RequestParam(value = "email") String email,
                              @RequestParam(value = "expirationTime") Long expirationTime,
                              Model model) {
        UserUnverified user = userUnverifiedService.verifyExpiration(token, email, expirationTime);
        model.addAttribute("token", token);
        model.addAttribute("email", user.getEmail());
        return "login-verify";
    }

    @PostMapping("/login/verify")
    public String tokenLogin(@RequestParam(value = "userName") String userName,
                             @RequestParam(value = "email") String email,
                             @RequestParam(value = "password") String password,
                             @RequestParam(value = "token") String token,
                             HttpServletResponse response) {
        User user = userUnverifiedService.UpdateByPassword(userName, email, token, password);
        response.addCookie(new Cookie("token", user.getToken()) {{
            setPath("/");
        }});
        return "redirect:/";
    }
}
