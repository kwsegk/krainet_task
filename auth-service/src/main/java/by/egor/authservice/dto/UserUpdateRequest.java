package by.egor.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class UserUpdateRequest {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}
