package by.egor.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreatedNotificationRequest {
    private String username;
    private String password;
    private String userEmail;
    private List<String> adminEmails;

}
