package by.egor.notificationservice.controller;

import by.egor.notificationservice.dto.NotificationRequest;
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

    @PostMapping("/send")
    public ResponseEntity<Void> send(@RequestBody NotificationRequest request) {
        log.info("Received notification request: action={}, username={}",
                request.getAction(), request.getUsername());
        emailService.sendToAdmins(request);
        return ResponseEntity.ok().build();
    }
}