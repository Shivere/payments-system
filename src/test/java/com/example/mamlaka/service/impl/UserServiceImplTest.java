package com.example.mamlaka.service.impl;

import com.example.mamlaka.dto.AuthenticationRequestDto;
import com.example.mamlaka.dto.AuthenticationResponseDto;
import com.example.mamlaka.dto.UserDto;
import com.example.mamlaka.entity.User;
import com.example.mamlaka.exception.UserAlreadyExistsException;
import com.example.mamlaka.mapper.UserMapper;
import com.example.mamlaka.repository.UserRepository;
import com.example.mamlaka.security.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private MyUserDetailsServiceImpl userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser_Success() {
        // Arrange
        UserDto userDto = new UserDto("john.doe", "password123", Set.of("ROLE_USER", "ROLE_ADMIN"));

        when(passwordEncoder.encode(anyString())).thenReturn("encodedpassword");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // Act
        userServiceImpl.registerUser(userDto);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    public void testRegisterUser_UserAlreadyExists() {
        // Arrange
        UserDto userDto = new UserDto("john.doe", "password123", Set.of("ROLE_USER", "ROLE_ADMIN"));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> userServiceImpl.registerUser(userDto));

        verify(userRepository, never()).save(any(User.class));
    }

//    @Test
//    public void testLogin_Success() throws Exception {
//        // Arrange
//        AuthenticationRequestDto authenticationRequestDto = new AuthenticationRequestDto();
//        authenticationRequestDto.setUsername("testuser");
//        authenticationRequestDto.setPassword("password");
//
//        UserDetails userDetails = mock(UserDetails.class);
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
//        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
//        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("token");
//
//        // Act
//        AuthenticationResponseDto responseDto = userServiceImpl.login(authenticationRequestDto);
//
//        // Assert
//        assertNotNull(responseDto);
//        assertEquals("token", responseDto.getAccessToken());
//        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
//        verify(jwtUtil, times(1)).generateToken(userDetails);
//    }

    @Test
    public void testLogin_IncorrectCredentials() {
        // Arrange
        AuthenticationRequestDto authenticationRequestDto = new AuthenticationRequestDto();
        authenticationRequestDto.setUsername("testuser");
        authenticationRequestDto.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> userServiceImpl.login(authenticationRequestDto));
        assertEquals("Incorrect username or password", exception.getMessage());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, never()).generateToken(any(UserDetails.class));
    }

    @AfterEach
    public void tearDown() {
        reset(userRepository, passwordEncoder, authenticationManager, userDetailsService, jwtUtil);
    }
}
