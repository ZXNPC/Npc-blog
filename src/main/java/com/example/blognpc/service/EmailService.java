package com.example.blognpc.service;

import com.example.blognpc.enums.LoginErrorCode;
import com.example.blognpc.exception.CustomizeException;
import com.example.blognpc.exception.LoginException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

@Service
@Slf4j
public class EmailService {
    @Autowired
    private Environment env;

    private Session session;

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", env.getProperty("spring.mail.host"));
        props.setProperty("mail.smtp.auth", "true");

        session = Session.getInstance(props);
        session.setDebug(false);
    }

    public MimeMessage createMimeMessage(String fromEmail,
                                         String toEmail,
                                         String subject,
                                         MimeMultipart mimeMultipart,
                                         Date sendDate) throws MessagingException, UnsupportedEncodingException {
        return createMimeMessage(fromEmail, toEmail, null, null, subject, mimeMultipart, sendDate);
    }

    public MimeMessage createMimeMessage(String fromEmail,
                                         String toEmail,
                                         String ccEmail,
                                         String bccEmail,
                                         String subject,
                                         MimeMultipart mimeMultipart,
                                         Date sendDate) throws UnsupportedEncodingException, MessagingException {
            // ????????????
            MimeMessage message = new MimeMessage(session);

            // ???????????????
            message.setFrom(new InternetAddress(fromEmail, "?????????", "UTF-8"));

            // ???????????????????????????????????????
            if (StringUtils.isBlank(toEmail + ccEmail + bccEmail))
                throw new MessagingException("???????????????????????????????????????????????????");
            if (StringUtils.isNotBlank(toEmail))
                message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(toEmail, "?????????"));
            if (StringUtils.isNotBlank(ccEmail))
                message.setRecipient(MimeMessage.RecipientType.CC, new InternetAddress(ccEmail, "?????????"));
            if (StringUtils.isNotBlank(bccEmail))
                message.setRecipient(MimeMessage.RecipientType.BCC, new InternetAddress(bccEmail, "?????????"));

            // ??????????????????
            message.setSubject(subject, "UTF-8");

            // ??????????????????
            message.setContent(mimeMultipart);

            // ??????????????????
            message.setSentDate(sendDate);

            // ????????????
            message.saveChanges();

            return message;
    }

    public void sendEmail(MimeMessage message, String fromEmail, String fromEmailCode) throws MessagingException {
            Transport transport = session.getTransport();
            transport.connect(fromEmail, fromEmailCode);

            // ????????????
            transport.sendMessage(message, message.getAllRecipients());

            // ????????????
            transport.close();
    }
}
