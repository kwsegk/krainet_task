package by.egor.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeNotificationRequest {
    private String username;
    private String email;
    private String oldPassword;
    private String newPassword;
    private List<String> adminEmails;
}