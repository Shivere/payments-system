package com.example.mamlaka.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationResponseDtoTest {

    private AuthenticationResponseDto authenticationResponseDto;

    @BeforeEach
    public void setUp() {
        authenticationResponseDto = new AuthenticationResponseDto();
    }

    @Test
    public void testSetAndGetUsername() {
        String expectedUsername = "admin";
        authenticationResponseDto.setUsername(expectedUsername);

        // Check if the username is set and retrieved correctly
        assertEquals(expectedUsername, authenticationResponseDto.getUsername());
    }

    @Test
    public void testSetAndGetRoles() {
        Set<String> expectedRoles = new HashSet<>();
        expectedRoles.add("ROLE_ADMIN");
        expectedRoles.add("ROLE_USER");

        authenticationResponseDto.setRoles(expectedRoles);

        // Check if roles are set and retrieved correctly
        assertEquals(expectedRoles, authenticationResponseDto.getRoles());
    }

    @Test
    public void testSetAndGetAccessToken() {
        String expectedToken = "sampleAccessToken";
        authenticationResponseDto.setAccessToken(expectedToken);

        // Check if the access token is set and retrieved correctly
        assertEquals(expectedToken, authenticationResponseDto.getAccessToken());
    }

    @Test
    public void testSetAndGetTokenExpiryDate() {
        Date expectedExpiryDate = new Date();
        authenticationResponseDto.setTokenExpiryDate(expectedExpiryDate);

        // Check if the token expiry date is set and retrieved correctly
        assertEquals(expectedExpiryDate, authenticationResponseDto.getTokenExpiryDate());
    }

    @Test
    public void testAllFieldsTogether() {
        String expectedUsername = "admin";
        Set<String> expectedRoles = new HashSet<>();
        expectedRoles.add("ROLE_ADMIN");
        expectedRoles.add("ROLE_USER");
        String expectedToken = "sampleAccessToken";
        Date expectedExpiryDate = new Date();

        authenticationResponseDto.setUsername(expectedUsername);
        authenticationResponseDto.setRoles(expectedRoles);
        authenticationResponseDto.setAccessToken(expectedToken);
        authenticationResponseDto.setTokenExpiryDate(expectedExpiryDate);

        // Check all fields are correctly set and retrieved
        assertEquals(expectedUsername, authenticationResponseDto.getUsername());
        assertEquals(expectedRoles, authenticationResponseDto.getRoles());
        assertEquals(expectedToken, authenticationResponseDto.getAccessToken());
        assertEquals(expectedExpiryDate, authenticationResponseDto.getTokenExpiryDate());
    }
}
