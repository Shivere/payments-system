package com.example.mamlaka.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class AuthenticationResponseDto {
    @Schema(
            description = "Username", example = "jane@doe"
    )
    private String username;

    @Schema(
            description = "Roles", example = "[ROLE]"
    )
    private Set<String> roles;

    @Schema(
            description = "Access token", example = ""
    )
    private String accessToken;

    @Schema(
            description = "Token expiry date", example = ""
    )
    private Date tokenExpiryDate;
}
