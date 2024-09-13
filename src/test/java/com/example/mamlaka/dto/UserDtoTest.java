package com.example.mamlaka.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserDtoTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        // Initialize the validator for testing
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidUserDto() {
        // Create a valid UserDto object
        UserDto userDto = new UserDto("john.doe", "password123", Set.of("ROLE_USER", "ROLE_ADMIN"));

        // Validate the DTO
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        // There should be no validation errors
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    public void testNullUsernameValidation() {
        // Create a UserDto with a null username
        UserDto userDto = new UserDto(null, "password123", Set.of("ROLE_USER"));

        // Validate the DTO
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        // There should be one validation error for the null username
        assertEquals(1, violations.size());

        // Check that the error is specifically about the username field
        ConstraintViolation<UserDto> violation = violations.iterator().next();
        assertEquals("Username is required", violation.getMessage());
        assertEquals("username", violation.getPropertyPath().toString());
    }

    @Test
    public void testNullPasswordValidation() {
        // Create a UserDto with a null password
        UserDto userDto = new UserDto("john.doe", null, Set.of("ROLE_USER"));

        // Validate the DTO
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        // There should be one validation error for the null password
        assertEquals(1, violations.size());

        // Check that the error is specifically about the password field
        ConstraintViolation<UserDto> violation = violations.iterator().next();
        assertEquals("Password is required", violation.getMessage());
        assertEquals("password", violation.getPropertyPath().toString());
    }

    @Test
    public void testNullUsernameAndPasswordValidation() {
        // Create a UserDto with both fields null
        UserDto userDto = new UserDto(null, null, Set.of("ROLE_USER"));

        // Validate the DTO
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        // There should be two validation errors for both fields
        assertEquals(2, violations.size());
    }

    @Test
    public void testSetAndGetRoles() {
        // Create a UserDto and set roles
        UserDto userDto = new UserDto("john.doe", "password123", Set.of("ROLE_USER"));

        // Check that roles are set and retrieved correctly
        assertEquals(Set.of("ROLE_USER"), userDto.getRoles());

        // Update roles
        userDto.setRoles(Set.of("ROLE_ADMIN"));
        assertEquals(Set.of("ROLE_ADMIN"), userDto.getRoles());
    }
}
