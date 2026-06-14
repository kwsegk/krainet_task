package by.egor.notificationservice.controller;

import by.egor.notificationservice.dto.UserCreatedNotificationRequest;
import by.egor.notificationservice.dto.PasswordChangeNotificationRequest;
import by.egor.notificationservice.dto.UserDeletedNotificationRequest;
import by.egor.notificationservice.dto.UserUpdatedNotificationRequest;
import by.egor.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/send-created")
    public ResponseEntity<Void> send(@RequestBody UserCreatedNotificationRequest request) {
        log.info("User created notification: {}", request.getUsername());
        emailService.sendUserCreatedNotification(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-password-changed")
    public ResponseEntity<Void> sendPasswordChangeNotification(@RequestBody PasswordChangeNotificationRequest request) {
        log.info("Received password change notification: username={}",
                request.getUsername());
        emailService.sendPasswordChangeNotification(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-updated")
    public ResponseEntity<Void> sendUpdated(@RequestBody UserUpdatedNotificationRequest request) {
        log.info("User updated notification: username={}", request.getOldUsername());
        emailService.sendUserUpdatedNotification(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-deleted")
    public ResponseEntity<Void> sendDeleted(@RequestBody UserDeletedNotificationRequest request) {
        log.info("User deleted notification: username={}", request.getUsername());
        emailService.sendUserDeletedNotification(request);
        return ResponseEntity.ok().build();
    }

}