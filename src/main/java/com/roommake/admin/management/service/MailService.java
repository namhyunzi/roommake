package com.roommake.admin.management.service;

import com.roommake.user.exception.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;        // 이메일 발송

    @Value("${spring.mail.username}")
    private String emailFrom;

    public String qnaHtmlTemplate(String title) throws EmailException {

        ClassPathResource resource = new ClassPathResource("templates/admin/management/answer-email.html");
        String htmlTemplate = null;
        try {
            htmlTemplate = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            System.err.println("이메일 템플릿을 로드하는 도중 오류가 발생했습니다.");
            ex.printStackTrace();
        }
        return htmlTemplate.replace("title", title);
    }

    public void sendEmail(String to, String subject, String html) throws EmailException {

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(emailFrom);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException ex) {
            throw new EmailException("이메일 발송 중 오류 발생", ex);
        }
    }
}
