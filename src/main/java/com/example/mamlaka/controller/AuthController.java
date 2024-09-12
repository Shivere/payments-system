package com.example.mamlaka.controller;

import com.example.mamlaka.constants.PaymentsConstants;
import com.example.mamlaka.dto.*;
import com.example.mamlaka.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Shivere
 */

@Tag(
        name = "CRUD REST APIs for Payments at Mamlaka",
        description = "CRUD REST APIs at Mamlaka to CREATE AND GET TRANSACTIONS"
)
@RestController
@RequestMapping(path="/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
@Validated
public class AuthController {

    private IUserService userService;

    @Operation(
            summary = "login REST API",
            description = "REST API to login user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status SUCCESS"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> authenticateUser(@Valid @RequestBody AuthenticationRequestDto authenticationRequest) throws Exception {

        AuthenticationResponseDto authenticationResponseDto = userService.login(authenticationRequest);
        return ResponseEntity.status(HttpStatus.OK).body(authenticationResponseDto);
    }



    @Operation(
            summary = "Register user REST API",
            description = "REST API to register users"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status CREATED"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @PostMapping("/register")
    public ResponseEntity<ResponseDto> registerUser(@Valid @RequestBody UserDto userDto) {
        userService.registerUser(userDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(PaymentsConstants.STATUS_201, PaymentsConstants.MESSAGE_201));
    }
}

