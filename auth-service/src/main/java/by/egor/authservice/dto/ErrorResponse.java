package by.egor.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {

    @Schema(
            description = "Код ошибки",
            example = "404"
    )
    private int status;

    @Schema(
            description = "Сообщение ошибки",
            example = "User not found with id: 1"
    )
    private String message;

    @Schema(
            description = "Время ошибки",
            example = "2024-06-08T12:00:00"
    )
    private LocalDateTime timestamp;
}
