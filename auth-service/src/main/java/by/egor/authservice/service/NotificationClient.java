package by.egor.authservice.service;

import by.egor.authservice.dto.NotificationRequest;
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
    public void notify(String action, String username, String password,
                       String email, List<String> adminEmails) {
        try {
            NotificationRequest request = new NotificationRequest(
                    action, username, password, email, adminEmails
            );
            restTemplate.postForEntity(
                    notificationServiceUrl + "/api/notifications/send",
                    request,
                    Void.class
            );
            log.info("Notification sent: action={}, username={}", action, username);
        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage());
        }
    }
}

