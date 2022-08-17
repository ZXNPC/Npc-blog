package com.example.blognpc.controller;

import com.example.blognpc.cache.UserWaitingMap;
import com.example.blognpc.model.User;
import com.example.blognpc.model.UserWaiting;
import com.example.blognpc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
public class VerifyController {
    @Autowired
    private UserService userService;

    @GetMapping("/verify")
    public String emailVerify(@PathVariable(value = "token") String token,
                             Model model) {
        model.addAttribute("token", token);
        return "verify";
    }

    @PostMapping("/verify")
    public String tokenLogin(@PathVariable(value = "password") String password,
                             @PathVariable(value = "token") String token,
                             HttpServletResponse response) {
        User user = userService.Update(token, password);
        response.addCookie(new Cookie("token", user.getToken()));
        return "redirect:/";
    }
}
