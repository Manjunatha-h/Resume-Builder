package in.mjunth.resumebuilderjava.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {


    private String from = "manjunthforgdg@gmail.com";
    private final JavaMailSender javaMailSender;

    public void sendHtmlContent(String to, String subject, String html) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");

        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html,true);

        javaMailSender.send(message);

    }

    public void sendEmailWithAttachment(String recipientEmail, String emailSubject, String emailBody, byte[] bytes, String filename) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message,true);

        messageHelper.setFrom(from);
        messageHelper.setTo(recipientEmail);
        messageHelper.setSubject(emailSubject);
        messageHelper.setText(emailBody,true);
        messageHelper.addAttachment(filename,new ByteArrayResource(bytes));

        javaMailSender.send(message);
    }
}
