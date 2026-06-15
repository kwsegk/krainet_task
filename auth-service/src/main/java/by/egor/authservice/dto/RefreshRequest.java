package by.egor.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshRequest {

    @Schema(
            description = "Refresh токен",
            example = "7c819c74-172e-4828-938e-19987e92ebbf"
    )
    @NotBlank(message = "Refresh токен не может быть пустым")
    private String refreshToken;
}
