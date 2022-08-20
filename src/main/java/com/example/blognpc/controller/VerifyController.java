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
        Object info = userService.verifyExpiration(token, expirationTime);
        model.addAttribute("token", token);
        if (info.getClass().equals(ResultDTO.class)) {
            ResultDTO resultDTO = (ResultDTO) info;
            model.addAttribute("email", email);
            model.addAttribute("resultDTO", resultDTO);
            /*  TOKEN_NOT_FOUND(5, "个人令牌错误，过期或者未注册"),
             *  TOKEN_REACH_EXPIRATION(6, "个人令牌已过期，尝试重新注册"),
             *  EXPIRATION_UNCORRECT(7, "过期时间不匹配，请不要随意修改链接"),
             *  EMAIL_ALREADY_VERIFIED(8, "邮箱已验证，不要再点这个链接了啊喂")  */
            return "verify";
        }
        else {
            User user = (User) info;
            model.addAttribute("userName", user.getName());
            return "verify";
        }
    }

    @PostMapping("/verify")
    public String tokenLogin(@RequestParam(value = "password") String password,
                             @RequestParam(value = "token") String token,
                             HttpServletResponse response) {
        User user = userService.UpdateByPassword(token, password);
        response.addCookie(new Cookie("token", user.getToken()));
        return "redirect:/";
    }
}
