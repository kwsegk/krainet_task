package by.egor.notificationservice.service;

import by.egor.notificationservice.dto.UserCreatedNotificationRequest;
import by.egor.notificationservice.dto.PasswordChangeNotificationRequest;
import by.egor.notificationservice.dto.UserDeletedNotificationRequest;
import by.egor.notificationservice.dto.UserUpdatedNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendUserCreatedNotification(UserCreatedNotificationRequest request) {
        String subject = String.format("Создан пользователь %s", request.getUsername());
        String text = String.format(
                "Создан пользователь с именем пользователя- %s, паролем - %s и почтой - %s",
                request.getUsername(),
                request.getPassword(),
                request.getUserEmail()
        );
        sendToAll(subject, text, request.getAdminEmails());
    }

    public void sendUserUpdatedNotification(UserUpdatedNotificationRequest request) {
        String subject = String.format("Изменён пользователь %s", request.getOldUsername());
        String text = String.format(
                "Изменён пользователь.%n" +
                        "Старое имя пользователя: %s -> Новое имя пользователя: %s%n" +
                        "Старый email: %s -> Новый email: %s%n" +
                        "Старое имя: %s ->  Новое имя: %s%n" +
                        "Старая фамилия: %s ->  Новая фамилия: %s",
                request.getOldUsername(), request.getNewUsername(),
                request.getOldEmail(), request.getNewEmail(),
                request.getOldFirstName(), request.getNewFirstName(),
                request.getOldLastName(), request.getNewLastName()
        );
        sendToAll(subject, text, request.getAdminEmails());
    }

    public void sendPasswordChangeNotification(PasswordChangeNotificationRequest request) {
        String subject  = String.format("Изменен пароль пользователя %s", request.getUsername());
        String text = String.format(
                "Изменён пользователь %s с почтой %s.%n" +
                        "Старый пароль: %s ->  Новый пароль: %s",
                request.getUsername(), request.getEmail(),
                request.getOldPassword(), request.getNewPassword()
        );
        sendToAll(subject, text, request.getAdminEmails());
    }
    public void sendUserDeletedNotification(UserDeletedNotificationRequest request) {
        String subject = String.format("Удалён пользователь %s", request.getUsername());
        String text = String.format(
                "Удалён пользователь с именем - %s и почтой - %s",
                request.getUsername(),
                request.getEmail()
        );
        sendToAll(subject, text, request.getAdminEmails());
    }

    public void sendToAll(String subject, String text, List<String> adminEmails) {
        for (String adminEmail : adminEmails) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(adminEmail);
                message.setSubject(subject);
                message.setText(text);
                javaMailSender.send(message);
                log.info("Notification sent to {}", adminEmail);
            } catch (Exception e) {
                log.error("Failed to send to {}: {}", adminEmail, e.getMessage());
            }
        }
    }
}
