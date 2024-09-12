package com.example.mamlaka.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Schema(
        name = "User",
        description = "Schema to hold user information"
)
@Data
@AllArgsConstructor
public class UserDto {
    @Schema(
            description = "Username of the user"
    )
    @NotNull(message = "Username is required")
    private String username;

    @Schema(
            description = "Password of the user"
    )
    @NotNull(message = "Password is required")
    private String password;

    @Schema(
            description = "User's roles"
    )
    private Set<String> roles;
}
