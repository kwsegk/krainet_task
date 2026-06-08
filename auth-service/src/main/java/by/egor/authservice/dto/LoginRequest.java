package by.egor.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @Schema(
            description = "Имя пользователя (логин)",
            example = "Andrey"
    )
    @NotBlank(message = "Username не может быть пустым")
    private String username;

    @Schema(
            description = "Пароль пользователя",
            example = "12345678"
    )
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;
}
