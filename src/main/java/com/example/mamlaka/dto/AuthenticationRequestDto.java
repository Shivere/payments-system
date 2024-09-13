package com.example.mamlaka.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(
        name = "Authentication request",
        description = "Schema to hold Authentication Request information"
)
public class AuthenticationRequestDto {
    @Schema(
            description = "Username", example = "john@doe.com"
    )
    @NotNull(message = "Username is required")
    private String username;

    @Schema(
            description = "Password", example = "1234"
    )
    @NotNull(message = "Password is required")
    private String password;
}
