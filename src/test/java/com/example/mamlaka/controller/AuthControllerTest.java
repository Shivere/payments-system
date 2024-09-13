package com.example.mamlaka.controller;

import com.example.mamlaka.constants.PaymentsConstants;
import com.example.mamlaka.dto.*;
import com.example.mamlaka.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AuthControllerTest {

    @Mock
    private IUserService userService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAuthenticateUser_Success() throws Exception {
        AuthenticationRequestDto requestDto = new AuthenticationRequestDto();
        requestDto.setUsername("testUser");
        requestDto.setPassword("password");

        AuthenticationResponseDto responseDto = new AuthenticationResponseDto();
        responseDto.setAccessToken("testToken");

        when(userService.login(any(AuthenticationRequestDto.class))).thenReturn(responseDto);

        ResponseEntity<AuthenticationResponseDto> response = authController.authenticateUser(requestDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testToken", response.getBody().getAccessToken());
    }

    @Test
    public void testAuthenticateUser_ValidationError() {
        AuthenticationRequestDto requestDto = new AuthenticationRequestDto();
        // Missing required fields should trigger validation error

        try {
            authController.authenticateUser(requestDto);
        } catch (Exception ex) {
            assertEquals(MethodArgumentNotValidException.class, ex.getClass());
        }
    }

    @Test
    public void testRegisterUser_Success() {
        UserDto userDto = new UserDto("testUser", "password", Set.of("ROLE_ADMIN"));


        ResponseDto responseDto = new ResponseDto(PaymentsConstants.STATUS_201, PaymentsConstants.MESSAGE_201);

        ResponseEntity<ResponseDto> response = authController.registerUser(userDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
    }

    @Test
    public void testRegisterUser_ValidationError() {
        UserDto userDto = new UserDto("testUser", "password", Set.of("ROLE_ADMIN"));
        // Missing required fields should trigger validation error

        try {
            authController.registerUser(userDto);
        } catch (Exception ex) {
            assertEquals(MethodArgumentNotValidException.class, ex.getClass());
        }
    }
}
