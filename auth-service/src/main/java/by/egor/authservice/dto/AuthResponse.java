package by.egor.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    @Schema(
            description = "Access токен, действует 15 минут, не хранится в БД",
            example = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiI5In0.abc123"
    )
    private String accessToken;

    @Schema(
            description = "Refresh токен, действует 30 дней, хранится в БД",
            example = "7c819c74-172e-4828-938e-19987e92ebbf"
    )
    private String refreshToken;
}
