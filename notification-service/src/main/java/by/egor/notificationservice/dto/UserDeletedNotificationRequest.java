package by.egor.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDeletedNotificationRequest {
    private String username;
    private String email;
    private List<String> adminEmails;
}