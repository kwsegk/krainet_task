package by.egor.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatedNotificationRequest {
    private String oldUsername;
    private String newUsername;
    private String oldEmail;
    private String newEmail;
    private String oldFirstName;
    private String newFirstName;
    private String oldLastName;
    private String newLastName;
    private List<String> adminEmails;
}