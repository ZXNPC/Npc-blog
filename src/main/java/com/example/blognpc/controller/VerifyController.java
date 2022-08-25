package com.example.blognpc.controller;

import com.example.blognpc.dto.ResultDTO;
import com.example.blognpc.model.User;
import com.example.blognpc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;

@Controller
public class VerifyController {
    @Autowired
    private UserService userService;

    @GetMapping("/verify")
    public String emailVerify(@RequestParam(value = "token") String token,
                             @RequestParam(value = "email") String email,
                             @RequestParam(value = "expirationTime") Long expirationTime,
                             Model model) {
        User user = userService.verifyExpiration(token, email, expirationTime);
        model.addAttribute("token", token);
        model.addAttribute("userName", user.getName());
        return "verify";
    }

    @PostMapping("/verify")
    public String tokenLogin(@RequestParam(value = "password") String password,
                             @RequestParam(value = "password_repeat") String passwordRepeat,
                             @RequestParam(value = "token") String token,
                             HttpServletResponse response) {
        assert password.equals(passwordRepeat);
        User user = userService.UpdateByPassword(token, password);
        response.addCookie(new Cookie("token", user.getToken()){{setPath("/");}});
        return "redirect:/";
    }
}
