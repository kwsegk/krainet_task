package by.egor.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    @Schema(
            description = "Текущий пароль",
            example = "12345678"
    )
    @NotBlank(message = "Старый пароль не может быть пустым")
    private String oldPassword;

    @Schema(
            description = "Новый пароль",
            example = "newpass123"
    )
    @NotBlank(message = "Новый пароль не может быть пустым")
    @Size(min = 8, max = 100, message = "Новый пароль должен быть не короче 8 символов")
    private String newPassword;
}
