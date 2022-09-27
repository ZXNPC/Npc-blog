package com.example.blognpc.controller;

import com.example.blognpc.dto.ResultDTO;
import com.example.blognpc.enums.LoginErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.exception.LoginException;
import com.example.blognpc.model.User;
import com.example.blognpc.model.UserUnverified;
import com.example.blognpc.service.EmailService;
import com.example.blognpc.service.UserService;
import com.example.blognpc.service.UserUnverifiedService;
import com.example.blognpc.utils.CalendarUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

@Controller
@Slf4j
public class LoginController {
    @Value("${signup.user.expiration}")
    private Long expiration;
    @Value("${spring.mail.username}")
    private String fromEmail;
    @Value("${spring.mail.password}")
    private String fromEmailCode;

    @Autowired
    private UserService userService;
    @Autowired
    private UserUnverifiedService userUnverifiedService;
    @Autowired
    private EmailService emailService;

    @GetMapping({"/login", "/login/signin/", "/login/signup"})
    public String login(
//            Model model,
//            @ModelAttribute(value = "resultDTO") ResultDTO resultDTO,
//            @ModelAttribute(value = "signin_email") String email,
//            @ModelAttribute(value = "signin_password") String password,
//            @ModelAttribute(value = "fromSignin") String fromSignin
    ) {
//        model.addAttribute("resultDTO", resultDTO);
//        model.addAttribute("email", email);
//        model.addAttribute("signin_password", password);
//        model.addAttribute("fromSignin", fromSignin);
        return "login";
    }


    @PostMapping("/login/signin")
    public String signin(Model model,
                         @RequestParam(value = "email") String email,
                         @RequestParam(value = "signin_password") String password,
                         HttpServletResponse response,
                         RedirectAttributes redirectAttributes) {
        model.addAttribute("email", email);
        model.addAttribute("signin_password", password);

        User user = userService.passwordSignin(email, password);
        response.addCookie(new Cookie("token", user.getToken()) {{
            setPath("/");
        }});
        return "redirect:/";
    }

    @PostMapping("/login/signup")
    public String signup(Model model,
                         @RequestParam(value = "email") String toEmail,
                         @RequestParam(value = "signup_password", required = false) String password,
                         HttpServletRequest request) {
        model.addAttribute("email", toEmail);
        model.addAttribute("signup_password", password);

        String token = UUID.randomUUID().toString();

        UserUnverified user = new UserUnverified();
        user.setEmail(toEmail);
        user.setToken(token);
        user.setPassword(password);

        userUnverifiedService.saveByEmail(user);

        Long expirationTime = user.getGmtModified() + expiration;
        String url = String.join("/", Arrays.copyOfRange(request.getRequestURL().toString().split("/"), 0, 3))
                + "/login/verify?token=" + user.getToken() + "&email=" + toEmail + "&expirationTime=" + expirationTime;
        String welcome = CalendarUtils.welcome();
        String articleUrl = "http:blog.zx2624.co/?search=发送邮件";
        String articleHtml = "<a href=\"" + articleUrl + "\">点击跳转</a>";

        String subject = "NPC-BLOG 邮箱验证";
        MimeMultipart multipart = new MimeMultipart();

        MimeBodyPart part = new MimeBodyPart();
        try {
            part.setContent("<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css\"\n" +
                    "          integrity=\"sha384-HSMxcRTRxnN+Bdg0JdbxYKrThecOKuH5zCYotlSAcp1+c8xmyTe9GYg1l9a69psu\" crossorigin=\"anonymous\">\n" +
                    "    <style>\n" +
                    "        .text-desc {\n" +
                    "            font-size: 12px;\n" +
                    "            font-weight: normal;\n" +
                    "            color: #999;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<div class=\"container\">\n" +
                    "    <div class=\"jumbotron\">\n" +
                    "        <h1>NPC-BLOG</h1>\n" +
                    "        <p>Hi, " + ", " + welcome + "</p>\n" +
                    "        <p>点击下面的按钮来验证你的邮箱</p>\n" +
                    "        <p><a class=\"btn btn-primary btn-lg\" href=\"" + url + "\" role=\"button\">验证电子邮箱</a></p>\n" +
                    "        <div class=\"text-desc\">这是一封系统自动生成的邮件，请勿直接回复。</div>\n" +
                    "        <div class=\"text-desc\">想知道如何发这种炫酷的邮件吗？看看这篇文章 &rightarrow; " + articleHtml + "</div>\n" +
                    "    </div>\n" +
                    "</div>\n" +
                    "</body>\n" +
                    "</html>", "text/html;charset=UTF-8");
            multipart.addBodyPart(part);
            emailService.sendEmail(emailService.createMimeMessage(fromEmail, toEmail, subject, multipart, new Date()), fromEmail, fromEmailCode);
        } catch (MessagingException e) {
            // 邮件发送异常
            userUnverifiedService.deleteByEmail(user);
            log.error(e.getMessage());
            throw new LoginException(LoginErrorCode.EMAIL_SEND_EXCEPTION);
        } catch (UnsupportedEncodingException e) {
            userUnverifiedService.deleteByEmail(user);
            log.error(e.getMessage());
            throw new LoginException(LoginErrorCode.EMAIL_SEND_EXCEPTION);
        }


//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom("2946310156@qq.com");
//        message.setTo(email);
//        message.setSubject("NPC Blog 邮箱验证");
//        message.setText("此邮件为系统自动生成，请勿直接回复。\n请点击以下链接验证你的邮箱地址：\n" + url + "\n该链接将于"
//                + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(expirationTime) + "过期。\n" +
//                "你的个人登录token为：" + token);
//        try {
//            mailSender.send(message);
//        } catch (MailException e) {
////                e.printStackTrace();
//            throw new LoginException(LoginErrorCode.EMAIL_SEND_EXCEPTION);
//        }

//        redirectAttributes.addFlashAttribute("resultDTO", ResultDTO.okOf(toEmail));
//        redirectAttributes.addFlashAttribute("fromSignin", false);

        model.addAttribute("resultDTO", ResultDTO.okOf(toEmail));
        model.addAttribute("fromSignin", false);

        return "login";
    }
}
