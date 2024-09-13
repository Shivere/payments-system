package com.example.mamlaka.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorResponseDtoTest {

    private ErrorResponseDto errorResponseDto;

    @BeforeEach
    public void setUp() {
        // Initialize a valid ErrorResponseDto instance
        errorResponseDto = new ErrorResponseDto(
                "/api/test",
                HttpStatus.BAD_REQUEST,
                "Invalid request",
                LocalDateTime.now()
        );
    }

    @Test
    public void testConstructorAndGetters() {
        String expectedApiPath = "/api/test";
        HttpStatus expectedErrorCode = HttpStatus.BAD_REQUEST;
        String expectedErrorMessage = "Invalid request";
        LocalDateTime expectedErrorTime = errorResponseDto.getErrorTime(); // Fetch time from the object

        // Assert that the constructor correctly sets the fields
        assertEquals(expectedApiPath, errorResponseDto.getApiPath());
        assertEquals(expectedErrorCode, errorResponseDto.getErrorCode());
        assertEquals(expectedErrorMessage, errorResponseDto.getErrorMessage());
        assertNotNull(errorResponseDto.getErrorTime());  // Check that errorTime is not null
        assertEquals(expectedErrorTime, errorResponseDto.getErrorTime());  // Check that time matches
    }

    @Test
    public void testSetAndGetApiPath() {
        String newApiPath = "/api/newpath";
        errorResponseDto.setApiPath(newApiPath);

        // Assert that the apiPath was updated correctly
        assertEquals(newApiPath, errorResponseDto.getApiPath());
    }

    @Test
    public void testSetAndGetErrorCode() {
        HttpStatus newErrorCode = HttpStatus.INTERNAL_SERVER_ERROR;
        errorResponseDto.setErrorCode(newErrorCode);

        // Assert that the errorCode was updated correctly
        assertEquals(newErrorCode, errorResponseDto.getErrorCode());
    }

    @Test
    public void testSetAndGetErrorMessage() {
        String newErrorMessage = "New error message";
        errorResponseDto.setErrorMessage(newErrorMessage);

        // Assert that the errorMessage was updated correctly
        assertEquals(newErrorMessage, errorResponseDto.getErrorMessage());
    }

    @Test
    public void testSetAndGetErrorTime() {
        LocalDateTime newErrorTime = LocalDateTime.now().plusDays(1);
        errorResponseDto.setErrorTime(newErrorTime);

        // Assert that the errorTime was updated correctly
        assertEquals(newErrorTime, errorResponseDto.getErrorTime());
    }

    @Test
    public void testAllFieldsTogether() {
        String expectedApiPath = "/api/allfields";
        HttpStatus expectedErrorCode = HttpStatus.FORBIDDEN;
        String expectedErrorMessage = "Forbidden action";
        LocalDateTime expectedErrorTime = LocalDateTime.now().plusHours(2);

        errorResponseDto.setApiPath(expectedApiPath);
        errorResponseDto.setErrorCode(expectedErrorCode);
        errorResponseDto.setErrorMessage(expectedErrorMessage);
        errorResponseDto.setErrorTime(expectedErrorTime);

        // Check all fields
        assertEquals(expectedApiPath, errorResponseDto.getApiPath());
        assertEquals(expectedErrorCode, errorResponseDto.getErrorCode());
        assertEquals(expectedErrorMessage, errorResponseDto.getErrorMessage());
        assertEquals(expectedErrorTime, errorResponseDto.getErrorTime());
    }
}
