package by.egor.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class UserUpdateRequest {

    @Schema(
            description = "Имя пользователя (логин)",
            example = "andrey_new"
    )
    @NotBlank(message = "Username не может быть пустым")
    @Size(min = 3, max = 30, message = "Username должен быть от 3 до 30 символов")
    private String username;

    @Schema(
            description = "Email пользователя",
            example = "andrey_new@mail.com"
    )
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    @Schema(
            description = "Имя пользователя",
            example = "Андрей"
    )
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 30, message = "Имя должно быть от 2 до 30 символов")
    private String firstName;

    @Schema(description = "Фамилия пользователя", example = "Ivanov")
    @Size(max = 30, message = "Фамилия не может быть длиннее 30 символов")
    private String lastName;
}
