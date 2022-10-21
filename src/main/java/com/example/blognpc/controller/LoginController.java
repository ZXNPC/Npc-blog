package com.example.blognpc.controller;

import com.example.blognpc.dto.ResultDTO;
import com.example.blognpc.enums.LoginErrorCode;
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
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        String articleUrl = "http://blog.zx2624.co/?search=发送邮件";
        String articleHtml = "<a style=\"color: #999;\" href=\"" + articleUrl + "\">点击跳转</a>";

        String subject = "NPC-BLOG 邮箱验证";
        MimeMultipart multipart = new MimeMultipart();

        MimeBodyPart part = new MimeBodyPart();
        try {
            part.setContent("<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<div style=\"padding-right: 15px; padding-left: 15px; margin-right: auto; margin-left: auto; max-width: 1170px;\">\n" +
                    "    <div style=\"padding: 48px 60px; border-radius: 6px; margin-bottom: 30px; color: inherit; background-color: #eee;\">\n" +
                    "        <h1 style=\"margin-top: 10px; font-size: 63px; font-family: inherit; font-weight: 500; line-height: 1.1;\">NPC-BLOG</h1>\n" +
                    "        <p style=\"margin-bottom: 15px; font-size: 21px; font-weight: 200; margin: 0 0 10px;\">Hi, " + welcome + "</p>\n" +
                    "        <p style=\"margin-bottom: 15px; font-size: 21px; font-weight: 200; margin: 0 0 10px;\">点击下面的按钮来验证你的邮箱</p>\n" +
                    "        <p style=\"margin-bottom: 15px; font-size: 21px; font-weight: 200; margin: 0 0 10px;\">\n" +
                    "            <a style=\"color: white; background-color: #337ab7; border-color: #2e6da4; text-decoration: none; display: inline-block; margin-bottom: 0; font-weight: 400; text-align: center; white-space: nowrap; vertical-align: middle; -ms-touch-action: manipulation; touch-action: manipulation; cursor: pointer; background-image: none; border: 1px solid transparent; padding: 6px 12px; font-size: 18px; line-height: 1.3333333; border-radius: 6px; -webkit-user-select: none; -moz-user-select: none; -ms-user-select: none; user-select: none; padding: 10px 16px;\" href=\"" + url + "\" role=\"button\">验证电子邮箱</a>\n" +
                    "        </p>\n" +
                    "        <div style=\"font-size: 12px; font-weight: normal; color: #999;\">这是一封系统自动生成的邮件，请勿直接回复。</div>\n" +
                    "        <div style=\"font-size: 12px; font-weight: normal; color: #999;\">想知道如何发这种炫酷的邮件吗？看看这些文章 &rightarrow; " + articleHtml + "</div>\n" +
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

        model.addAttribute("resultDTO", ResultDTO.emailOkOf(toEmail));

        return "login";
    }
}
