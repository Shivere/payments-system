package com.example.mamlaka.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthenticationRequestDtoTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidAuthenticationRequestDto() {
        AuthenticationRequestDto requestDto = new AuthenticationRequestDto();
        requestDto.setUsername("john@doe.com");
        requestDto.setPassword("1234");

        Set<ConstraintViolation<AuthenticationRequestDto>> violations = validator.validate(requestDto);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void testNullUsername() {
        AuthenticationRequestDto requestDto = new AuthenticationRequestDto();
        requestDto.setPassword("1234");

        Set<ConstraintViolation<AuthenticationRequestDto>> violations = validator.validate(requestDto);

        assertEquals(1, violations.size());

        ConstraintViolation<AuthenticationRequestDto> violation = violations.iterator().next();
        assertEquals("Username is required", violation.getMessage());
        assertEquals("username", violation.getPropertyPath().toString());
    }

    @Test
    public void testNullPassword() {
        AuthenticationRequestDto requestDto = new AuthenticationRequestDto();
        requestDto.setUsername("john@doe.com");

        Set<ConstraintViolation<AuthenticationRequestDto>> violations = validator.validate(requestDto);

        assertEquals(1, violations.size());

        ConstraintViolation<AuthenticationRequestDto> violation = violations.iterator().next();
        assertEquals("Password is required", violation.getMessage());
        assertEquals("password", violation.getPropertyPath().toString());
    }

    @Test
    public void testNullUsernameAndPassword() {
        AuthenticationRequestDto requestDto = new AuthenticationRequestDto();

        Set<ConstraintViolation<AuthenticationRequestDto>> violations = validator.validate(requestDto);

        assertEquals(2, violations.size());

        boolean usernameError = false;
        boolean passwordError = false;

        for (ConstraintViolation<AuthenticationRequestDto> violation : violations) {
            if (violation.getPropertyPath().toString().equals("username")) {
                assertEquals("Username is required", violation.getMessage());
                usernameError = true;
            } else if (violation.getPropertyPath().toString().equals("password")) {
                assertEquals("Password is required", violation.getMessage());
                passwordError = true;
            }
        }

        assertTrue(usernameError);
        assertTrue(passwordError);
    }
}
