package org.example.barber_shop.Service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.mail.username}")
    private String from;
    @Async
    public void sendMailToken(String to, String subject, String token) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setFrom(new InternetAddress(from, applicationName));
            Context context = new Context();
            context.setVariable("token", token);
            context.setVariable("web_name", applicationName);
            String stringProcess = templateEngine.process("verify_mail", context);
            mimeMessageHelper.setText(stringProcess, true);
            mailSender.send(mimeMessage);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
