package com.example.mamlaka.service;

import com.example.mamlaka.dto.AuthenticationRequestDto;
import com.example.mamlaka.dto.AuthenticationResponseDto;
import com.example.mamlaka.dto.UserDto;
import com.example.mamlaka.service.impl.UserServiceImplTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class IUserServiceTest {

    @Mock
    private IUserService userService;

    @InjectMocks
    private UserServiceImplTest userServiceImpl; // Assuming IUserServiceImpl is the implementation of IUserService

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser_Success() {
        UserDto userDto = new UserDto("testUser", "password", Set.of("ROLE_ADMIN"));
        userDto.setUsername("testuser");
        userDto.setPassword("password");

        doNothing().when(userService).registerUser(any(UserDto.class));

        userService.registerUser(userDto);

        verify(userService, times(1)).registerUser(userDto);
    }

    @Test
    public void testRegisterUser_NullUserDto() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(null);
        });

        verify(userService, times(0)).registerUser(any(UserDto.class));
    }

    @Test
    public void testLogin_Success() throws Exception {
        AuthenticationRequestDto requestDto = new AuthenticationRequestDto();
        requestDto.setUsername("testuser");
        requestDto.setPassword("password");

        AuthenticationResponseDto responseDto = new AuthenticationResponseDto();
        responseDto.setAccessToken("token");

        when(userService.login(any(AuthenticationRequestDto.class))).thenReturn(responseDto);

        AuthenticationResponseDto result = userService.login(requestDto);

        assertNotNull(result);
        assertEquals("token", result.getAccessToken());
        verify(userService, times(1)).login(requestDto);
    }

    @Test
    public void testLogin_InvalidCredentials() throws Exception {
        AuthenticationRequestDto requestDto = new AuthenticationRequestDto();
        requestDto.setUsername("invaliduser");
        requestDto.setPassword("wrongpassword");

        when(userService.login(any(AuthenticationRequestDto.class))).thenThrow(new Exception("Invalid credentials"));

        Exception exception = assertThrows(Exception.class, () -> {
            userService.login(requestDto);
        });

        assertEquals("Invalid credentials", exception.getMessage());
        verify(userService, times(1)).login(requestDto);
    }

    @Test
    public void testLogin_NullRequestDto() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.login(null);
        });

        verify(userService, times(0)).login(any(AuthenticationRequestDto.class));
    }

    @AfterEach
    public void tearDown() {
        reset(userService);
    }
}
