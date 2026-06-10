package by.egor.notificationservice.service;

import by.egor.notificationservice.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendToAdmins(NotificationRequest notificationRequest) {
        String subject = String.format("%s пользователь $s",
                notificationRequest.getAction(), notificationRequest.getUsername());

        String text = String.format(
                "%s пользователь с именем - %s, паролем - %s и почтой - %s",
                notificationRequest.getAction(),
                notificationRequest.getUsername(),
                notificationRequest.getPassword(),
                notificationRequest.getUserEmail()
        );

        for (String adminEmail : notificationRequest.getAdminEmails()) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(adminEmail);
                message.setSubject(subject);
                message.setText(text);
                javaMailSender.send(message);
                log.info("Notification sent to {}", adminEmail);
            } catch (Exception e) {
                log.error("Failed to send notification to {}: {}", adminEmail, e.getMessage());
            }
        }
    }
}
