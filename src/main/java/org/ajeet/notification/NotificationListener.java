package org.ajeet.notification;

import org.ajeet.notification.event.UserLoggedInEvent;
import org.ajeet.notification.event.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Delivers notifications off the request thread so a slow/failed
 * provider never blocks or fails registration or login.
 */
@Component
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z").withZone(ZoneId.systemDefault());

    private final EmailService emailService;
    private final WhatsAppService whatsAppService;

    public NotificationListener(EmailService emailService, WhatsAppService whatsAppService) {
        this.emailService = emailService;
        this.whatsAppService = whatsAppService;
    }

    @Async
    @EventListener
    public void onUserRegistered(UserRegisteredEvent event) {
        try {
            emailService.send(
                    event.email(),
                    "Welcome to Notification System",
                    "Hi " + event.name() + ",\n\nYour account has been created successfully.\n\n— Notification System");
        } catch (Exception e) {
            log.warn("Welcome email failed for {}: {}", event.email(), e.getMessage());
        }

        try {
            whatsAppService.send(
                    event.phone(),
                    "Hi " + event.name() + ", welcome! Your account was created successfully.");
        } catch (Exception e) {
            log.warn("Welcome WhatsApp failed for {}: {}", event.phone(), e.getMessage());
        }
    }

    @Async
    @EventListener
    public void onUserLoggedIn(UserLoggedInEvent event) {
        String when = TIME_FORMAT.format(event.at());

        try {
            emailService.send(
                    event.email(),
                    "New login to your account",
                    "Hi " + event.name() + ",\n\nA login to your account was detected at "
                            + when + ".\n\nIf this wasn't you, change your password immediately.\n\n— Notification System");
        } catch (Exception e) {
            log.warn("Login alert email failed for {}: {}", event.email(), e.getMessage());
        }

        try {
            whatsAppService.send(
                    event.phone(),
                    "Security alert: a login to your account was detected at " + when
                            + ". If this wasn't you, please reset your password.");
        } catch (Exception e) {
            log.warn("Login alert WhatsApp failed for {}: {}", event.phone(), e.getMessage());
        }
    }
}
