package com.example.blognpc;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.blognpc.dto.PaginationDTO;
import com.example.blognpc.dto.QuestionDTO;
import com.example.blognpc.mapper.ArticleMapper;
import com.example.blognpc.mapper.QuestionExtMapper;
import com.example.blognpc.mapper.QuestionMapper;
import com.example.blognpc.mapper.UserMapper;
import com.example.blognpc.model.Article;
import com.example.blognpc.model.Question;
import com.example.blognpc.model.User;
import com.example.blognpc.service.QuestionService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.AnonymousCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.DigestUtils;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
class BlogNpcApplicationTestsCopy {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;
    @Autowired
    private UserMapper userMapper;

    @Value("${cloud.tencent.cam.capi.app-id}")
    private String appId;

    @Value("${cloud.tencent.cam.capi.secret-id}")
    private String secretId;

    @Value("${cloud.tencent.cam.capi.secret-key}")
    private String secretKey;

    @Value("${cloud.tencent.cos.bucket.bucket-name}")
    private String bucketName;

    @Value("${cloud.tencent.cos.bucket.region}")
    private String region;

    @Value("${cloud.tencent.cos.bucket.expires}")
    private Integer expires;

    @Test
    public void emailTest() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("@qq.com");
        message.setTo("@qq.com");
        message.setSubject("it is a test for spring boot");
        message.setText("你好，我是小黄，我正在测试发送邮件。");

        try {
            mailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void MD5Test() {
        String password = DigestUtils.md5DigestAsHex("".getBytes(StandardCharsets.UTF_8));
        System.out.println(password);
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        System.out.println(password);
    }

    @Test
    public void mapTest() {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "a");
        String s = map.get(2);
        if (s == null)
            System.out.println("www");
        else
            System.out.println("dwasdwasd");
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
        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(System.currentTimeMillis());
        System.out.print("今天是:"+today.get(Calendar.YEAR)+"年");
        System.out.print(today.get(Calendar.MONTH)+1+"月");//注意月的值从0开始，这里取出的值是7，实际上是8月，所以要加上1
        System.out.println(today.get(Calendar.DATE)+"日");
        System.out.print(today.get(Calendar.HOUR_OF_DAY)+"时");
        System.out.print(today.get(Calendar.MINUTE)+"分");
        System.out.print(today.get(Calendar.SECOND)+"秒");
        System.out.println("\t"+"（即下午"+today.get(Calendar.HOUR)+"时）");
        System.out.println("这个星期的第"+today.get(Calendar.DAY_OF_WEEK)+"天"+"（即星期三）");
        System.out.println("这个月的第"+today.get(Calendar.DAY_OF_MONTH)+"天");
        System.out.println("这一年的第"+today.get(Calendar.DAY_OF_YEAR)+"天");

        int a = 12;
        today.add(Calendar.DATE, a);
        System.out.print("再过"+a+"天是："+today.get(Calendar.YEAR)+"年");
        System.out.print(today.get(Calendar.MONTH)+1+"月");
        System.out.println(today.get(Calendar.DATE)+"日");

        today.set(2018,8,17);
        System.out.print(today.get(Calendar.YEAR)+"年的七夕节是：");
        System.out.println(today.get(Calendar.MONTH)+"月"+today.get(Calendar.DATE)+"日");
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

//    @Test
//    public void likeTest() {
//        String regex = Arrays.stream("java, php".split(",")).map(tag -> {
//            tag = tag.trim();
//            return "(" + tag + ",)|(" + tag + "$)";
//        }).collect(Collectors.joining("|"));
//        System.out.println(regex);
//        List<Question> questions = questionExtMapper.selectRegexp("tag", regex, 5L);
//        for (Question question : questions) {
//            System.out.println(question.toString());
//        }
//    }

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

//    @Test
//    public void removeExpiredTest() {
//        List<User> users = userMapper.selectList(null);
//        List<Long> userIds = users.stream().filter(user -> user.getComplete() == false).map(user -> user.getId()).collect(Collectors.toList());
//        System.out.println(userIds.toString());
//    }

    @Test
    public void tencentCloudTest() {
        COSCredentials cred = new AnonymousCOSCredentials();
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setRegion(new Region(region));
        clientConfig.setHttpProtocol(HttpProtocol.http);
        COSClient cosClient = new COSClient(cred, clientConfig);
        String key = "0c5f973a-a773-46fb-8617-426374a5c285.png";
        System.out.println(cosClient.getObjectUrl(bucketName, key));
    }

    @Test
    public void questionExtMapperTest() {
        List<Question> questions = questionExtMapper.selectRegexp(null, "title", "(w)", "gmt_create", 1,  0L, 20L);
        for (Question question : questions) {
            System.out.println(question);
        }
    }

    @Test
    public void getTClassTest() {
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<QuestionDTO>() {};
        ParameterizedType type = (ParameterizedType) paginationDTO.getClass().getGenericSuperclass();
        Class tClass = (Class) type.getActualTypeArguments()[0];
        System.out.println(tClass);
    }

    @Test
    public void selectCountTest() {
        System.out.println(articleMapper.selectCount(null).toString());
        System.out.println(articleMapper.selectCount(new QueryWrapper<>()).toString());
        System.out.println(articleMapper.selectCount(new QueryWrapper<Article>().eq(true, "creator", 52)).toString());
    }

    @Test
    public void nullEqualTest() {
        Long id = null;
        if (id == 0L)
            System.out.println("yes");
    }

    @Autowired
    private Environment env;
    @Test
    public void emailHtmlTest() {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", env.getProperty("spring.mail.host"));
        props.setProperty("mail.smtp.auth", "true");

        Session session = Session.getInstance(props);

        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress("2946310156@qq.com", "发件人", "UTF-8"));
            message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress("993023569@qq.com", "收件人", "UTF-8"));

            message.setSubject("test for spring boot");

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent("<div style='color: red'>red test</div>", "text/html;charset=UTF-8");

            MimeMultipart mimeMultipart = new MimeMultipart();
            mimeMultipart.addBodyPart(mimeBodyPart);

            message.setContent(mimeMultipart);

            message.setSentDate(new Date());

            message.saveChanges();

            Transport transport = session.getTransport();

            transport.connect("2946310156@qq.com", "rakbfhvsosjvddha");

            transport.sendMessage(message, message.getAllRecipients());

            transport.close();


        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }




//        message.setFrom("2946310156@qq.com");
//        message.setTo("993023569@qq.com");
//        message.setSubject("it is a test for spring boot");
//        message.setText("<div style='color: red'>red test</div>");

    }

    @Test
    public void envTest() {
        System.out.println(env.getProperty("github.client.id"));
    }

//    @Autowired
//    private TestService testService;
//
//    @Test
//    public void serviceConstructorTest() {
//        testService.print();
//    }

    @Test
    public void tryTest() {
        Integer i = 10;
        try {
            i = 100;
            throw new RuntimeException();
        } catch (RuntimeException e) {
            System.out.println(i);
        }
        System.out.println(i);
    }


}
