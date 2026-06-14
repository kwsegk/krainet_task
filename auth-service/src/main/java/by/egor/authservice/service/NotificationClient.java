package by.egor.authservice.service;

import by.egor.authservice.dto.PasswordChangeNotificationRequest;
import by.egor.authservice.dto.UserCreatedNotificationRequest;
import by.egor.authservice.dto.UserDeletedNotificationRequest;
import by.egor.authservice.dto.UserUpdatedNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationClient {

    private final RestTemplate restTemplate;

    @Value("${notification.service.url}")
    private String notificationServiceUrl;

    @Async
    public void notifyUserCreated(String username, String password,
                                  String email, List<String> adminEmails) {
        try {
            UserCreatedNotificationRequest request = new UserCreatedNotificationRequest(
                    username, password, email, adminEmails
            );
            restTemplate.postForEntity(
                    notificationServiceUrl + "/api/notifications/send-created",
                    request, Void.class
            );
            log.info("User created notification sent: username={}", username);
        } catch (Exception e) {
            log.error("Failed to send created notification: {}", e.getMessage());
        }
    }

    @Async
    public void notifyUserUpdated(String oldUsername, String newUsername,
                                  String oldEmail, String newEmail,
                                  String oldFirstName, String newFirstName,
                                  String oldLastName, String newLastName,
                                  List<String> adminEmails) {
        try {
            UserUpdatedNotificationRequest request = new UserUpdatedNotificationRequest(
                    oldUsername, newUsername, oldEmail, newEmail,
                    oldFirstName, newFirstName, oldLastName, newLastName, adminEmails
            );
            restTemplate.postForEntity(
                    notificationServiceUrl + "/api/notifications/send-updated",
                    request,
                    Void.class
            );
            log.info("User updated notification sent: username={}", newUsername);
        } catch (Exception e) {
            log.error("Failed to send updated notification: {}", e.getMessage());
        }
    }

    @Async
    public void notifyUserDeleted(String username, String email,
                                  List<String> adminEmails) {
        try {
            UserDeletedNotificationRequest request = new UserDeletedNotificationRequest(
                    username, email, adminEmails
            );
            restTemplate.postForEntity(
                    notificationServiceUrl + "/api/notifications/send-deleted",
                    request, Void.class
            );
            log.info("User deleted notification sent: username={}", username);
        } catch (Exception e) {
            log.error("Failed to send deleted notification: {}", e.getMessage());
        }
    }

    @Async
    public void notifyPasswordChanged(String username, String email,
                                      String oldPassword, String newPassword,
                                      List<String> adminEmails) {
        try {
            PasswordChangeNotificationRequest request = new PasswordChangeNotificationRequest(
                    username, email, oldPassword, newPassword, adminEmails
            );
            restTemplate.postForEntity(
                    notificationServiceUrl + "/api/notifications/send-password-changed",
                    request, Void.class
            );
            log.info("Password changed notification sent: username={}", username);
        } catch (Exception e) {
            log.error("Failed to send password changed notification: {}", e.getMessage());
        }
    }
}

