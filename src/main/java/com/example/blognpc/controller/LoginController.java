package com.example.blognpc.controller;

import com.example.blognpc.dto.ResultDTO;
import com.example.blognpc.enums.LoginErrorCode;
import com.example.blognpc.model.User;
import com.example.blognpc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

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
    @Value("${signup.user.expiration}")
    private Long expiration;
    @Autowired
    private UserService userService;
    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/login")
    public String login(Model model,
                        @ModelAttribute(value = "resultDTO") ResultDTO resultDTO,
                        @ModelAttribute(value = "signin_email") String email,
                        @ModelAttribute(value = "signin_password") String password,
                        @ModelAttribute(value = "from_signup") String fromSignup) {
        model.addAttribute("clientId", clientId);
        model.addAttribute("resultDTO", resultDTO);
        model.addAttribute("email", email);
        model.addAttribute("signin_password", password);
        model.addAttribute("from_signup", fromSignup);
        return "login";
    }

    @PostMapping("/login/signin")
    public String signin(@RequestParam(value = "email") String email,
                         @RequestParam(value = "signin_password") String password,
                         HttpServletResponse response,
                         RedirectAttributes redirectAttributes) {
        Object info = userService.passwordLogin(email, password);
        if (info.getClass().equals(User.class)) {
            // 用户认证完成，返回主页
            User user = (User) info;
            response.addCookie(new Cookie("token", user.getToken()));
            return "redirect:/";
        } else if (info.getClass().equals(ResultDTO.class)) {
            // 用户认证失败，返回错误信息
            ResultDTO resultDTO = (ResultDTO) info;
            redirectAttributes.addFlashAttribute("resultDTO", resultDTO);
            redirectAttributes.addFlashAttribute("signin_email", email);
            redirectAttributes.addFlashAttribute("signin_password", password);
            redirectAttributes.addFlashAttribute("from_signup", false);
            return "redirect:/login";
        }
        return null;
    }

    @PostMapping("/login/signup")
    public String signup(@RequestParam(value = "signup_user", required = false) String userName,
                         @RequestParam(value = "email") String email,
                         RedirectAttributes redirectAttributes,
                         HttpServletRequest request) {
        String token = UUID.randomUUID().toString();
        Object info = userService.saveByEmail(userName, email, token);
        if (info.getClass().equals(ResultDTO.class)) {
            // 邮箱已存在
            ResultDTO resultDTO = (ResultDTO) info;
            redirectAttributes.addFlashAttribute("resultDTO", resultDTO);
            redirectAttributes.addFlashAttribute("from_signup", true);
            return "redirect:/login";
        } else {
            // 邮箱不存在
            User user = (User) info;
            Long expirationTime = user.getGmtModified() + expiration;
            String url = String.join("/", Arrays.copyOfRange(request.getRequestURL().toString().split("/"), 0, 3))
                    + "/verify?token=" + user.getToken() + "&email=" + email + "&expirationTime=" + expirationTime;
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("2946310156@qq.com");
            message.setTo(email);
            message.setSubject("NPC Blog 邮箱验证");
            message.setText("此邮件为系统自动生成，请勿直接回复。\n请点击以下链接验证你的邮箱地址：\n" + url + "\n该链接将于"
                    + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(expirationTime) + "过期。\n" +
                    "你的个人登录token为：" + token);
            try {
                mailSender.send(message);
            } catch (MailException e) {
//                e.printStackTrace();
                ResultDTO resultDTO = ResultDTO.errorOf(LoginErrorCode.EMAIL_SEND_EXCEPTION);
                resultDTO.setMessage(e.getMessage());
                redirectAttributes.addFlashAttribute("resultDTO", resultDTO);
                redirectAttributes.addFlashAttribute("from_signup", true);
                return "redirect:/login";
            }
            redirectAttributes.addFlashAttribute("resultDTO", ResultDTO.okOf(email));
            redirectAttributes.addFlashAttribute("from_signup", true);
            return "redirect:/login";
        }
    }


}
