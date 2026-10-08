package org.ajeet.notification;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Sends notification mail through the company SMTP relay.
 * Configure MAIL_HOST / MAIL_USERNAME / MAIL_PASSWORD and MAIL_FROM.
 * Without MAIL_FROM, messages are logged instead of sent.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String from;
    private final String fromName;

    public EmailService(JavaMailSender mailSender,
                        @Value("${app.mail.from:}") String from,
                        @Value("${app.mail.from-name:Notification System}") String fromName) {
        this.mailSender = mailSender;
        this.from = from;
        this.fromName = fromName;
    }

    public void send(String to, String subject, String body) {
        if (!StringUtils.hasText(from)) {
            // No company SMTP configured — log instead of failing the request.
            log.info("[email:dev] to={} subject={} body={}", to, subject, body);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(from, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);
            mailSender.send(message);
            log.info("[email] sent to={} subject={}", to, subject);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to send email to " + to, e);
        }
    }
}
