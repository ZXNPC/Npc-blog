package com.example.blognpc;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blognpc.mapper.QuestionExtMapper;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
class BlogNpcApplicationTests {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;

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

//    @Test
//    public void selectAllTest() {
//        List<Question> questions = questionMapper.selectAll();
//        for (Question question : questions) {
//            System.out.println(question.toString());
//        }
//    }
//
//    @Test
//    public void selectSomeTest() {
//        List<Question> questions = questionMapper.selectSome(2);
//        for (Question question : questions) {
//            System.out.println(question.toString());
//        }
//    }

    @Test
    public void likeTest() {
        String regex = Arrays.stream("java, php".split(",")).map(tag -> {
            tag = tag.trim();
            return "(" + tag + ",)|(" + tag + "$)";
        }).collect(Collectors.joining("|"));
        System.out.println(regex);
        List<Question> questions = questionExtMapper.selectRegexp("tag", regex, 5L);
        for (Question question : questions) {
            System.out.println(question.toString());
        }
    }

    @Test
    public void customizeQueryTest() {
        String regex = "(java,)|(java$)|(php,)|(php$)";
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.last("where tag regexp '" + regex + "' limit 0, 10");
        List<Question> questions = questionMapper.selectList(queryWrapper);
        for (Question question : questions) {
            System.out.println(question.toString());
        }
    }
}
