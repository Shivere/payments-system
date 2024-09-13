package com.example.mamlaka.service.impl;

import com.example.mamlaka.entity.User;
import com.example.mamlaka.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class MyUserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MyUserDetailsServiceImpl myUserDetailsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoadUserByUsername_UserExists() {
        // Arrange
        User mockUser = new User();
        mockUser.setUsername("john.doe");
        mockUser.setPassword("password123");
        mockUser.setRoles(Set.of("USER", "ADMIN"));

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(mockUser));

        // Act
        UserDetails userDetails = myUserDetailsService.loadUserByUsername("john.doe");

        // Assert
        assertNotNull(userDetails);
        assertEquals("john.doe", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertEquals(2, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));

        // Verify that the repository was called with the correct username
        verify(userRepository, times(1)).findByUsername("john.doe");
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            myUserDetailsService.loadUserByUsername("nonexistent.user");
        });

        assertEquals("User not found with username: nonexistent.user", exception.getMessage());

        // Verify that the repository was called with the correct username
        verify(userRepository, times(1)).findByUsername("nonexistent.user");
    }

    @Test
    public void testLoadUserByUsername_WithNoRoles() {
        // Arrange
        User mockUser = new User();
        mockUser.setUsername("jane.doe");
        mockUser.setPassword("password123");
        mockUser.setRoles(Set.of()); // No roles assigned

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(mockUser));

        // Act
        UserDetails userDetails = myUserDetailsService.loadUserByUsername("jane.doe");

        // Assert
        assertNotNull(userDetails);
        assertEquals("jane.doe", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertEquals(0, userDetails.getAuthorities().size()); // No roles should result in 0 authorities

        // Verify that the repository was called with the correct username
        verify(userRepository, times(1)).findByUsername("jane.doe");
    }

    @AfterEach
    public void tearDown() {
        reset(userRepository);
    }
}
