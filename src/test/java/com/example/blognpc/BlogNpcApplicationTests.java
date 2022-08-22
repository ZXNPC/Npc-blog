package com.example.blognpc;

import com.example.blognpc.mapper.QuestionMapper;
import com.example.blognpc.model.Question;
import com.example.blognpc.service.QuestionService;
import org.apache.tomcat.util.security.MD5Encoder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.util.DigestUtils;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class BlogNpcApplicationTests {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionMapper questionMapper;
    @Test
    public void emailTest() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("2946310156@qq.com");
        message.setTo("993023569@qq.com");
        message.setSubject("it is a test for spring boot");
        message.setText("你好，我是小黄，我正在测试发送邮件。");

        try {
            mailSender.send(message);
        } catch (MailException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void MD5Test() {
        String password = DigestUtils.md5DigestAsHex("BFBD13568551878".getBytes(StandardCharsets.UTF_8));
        System.out.println(password);
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        System.out.println(password);
    }

    @Test
    public void mapTest() {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "a");
        map.remove(2);
        System.out.println(map);
    }

    @Test
    public void requestTest() {
        String url = "http://localhost:8887/login/signup";
        String[] split = url.split("/");
        String[] strings = Arrays.copyOfRange(split, 0, 3);
        System.out.println(String.join("/", strings));
    }

    @Test
    public void dateTest() {
        long currentTimeMillis = System.currentTimeMillis();
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTimeMillis));
    }

    @Test
    public void booleanTest() {
        Boolean t = true;
        System.out.printf(t.toString());
    }

    @Test
    public void selectByIdNullTest() {
        Question question = questionMapper.selectById(null);
    }
}
