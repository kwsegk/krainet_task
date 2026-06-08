package by.egor.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    @Schema(
            description = "Уникальный идентификатор пользователя",
            example = "5"
    )
    private Long id;

    @Schema(
            description = "Имя пользователя (логин)",
            example = "Andrey"
    )
    private String username;

    @Schema(
            description = "Почта пользователя",
            example = "andrey@gmail.com"
    )
    private String email;

    @Schema(
            description = "Имя пользователя",
            example = "Андрей"
    )
    private String firstName;

    @Schema(
            description = "Фамилия пользователя",
            example = "Иванов"
    )
    private String lastName;

    @Schema(
            description = "Роль пользователя",
            example = "USER"
    )
    private String role;
}
