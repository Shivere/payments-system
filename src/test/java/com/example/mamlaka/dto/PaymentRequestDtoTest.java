package com.example.mamlaka.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PaymentRequestDtoTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        // Initialize the validator for testing
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidPaymentRequestDto() {
        // Create a valid PaymentRequestDto
        PaymentRequestDto dto = new PaymentRequestDto();
        dto.setAmount(new BigDecimal("100.00"));
        dto.setPaymentMethod("Mpesa");
        dto.setPhoneNumber("0700000000");
        dto.setAccountNumber("1234567812345678");
        dto.setDescription("Payment for services");

        // Validate the DTO
        Set<ConstraintViolation<PaymentRequestDto>> violations = validator.validate(dto);

        // There should be no validation errors
        assertTrue(violations.isEmpty(), "Expected no validation errors");
    }

    @Test
    public void testNullAmountValidation() {
        // Create a PaymentRequestDto with a null amount
        PaymentRequestDto dto = new PaymentRequestDto();
        dto.setAmount(null);
        dto.setPaymentMethod("Mpesa");

        // Validate the DTO
        Set<ConstraintViolation<PaymentRequestDto>> violations = validator.validate(dto);

        // There should be one validation error for the missing amount
        assertEquals(1, violations.size());

        // Check that the error is specifically about the amount field
        ConstraintViolation<PaymentRequestDto> violation = violations.iterator().next();
        assertEquals("Amount is required", violation.getMessage());
        assertEquals("amount", violation.getPropertyPath().toString());
    }

    @Test
    public void testInvalidAmountValidation() {
        // Create a PaymentRequestDto with an invalid amount (less than 1.0)
        PaymentRequestDto dto = new PaymentRequestDto();
        dto.setAmount(new BigDecimal("0.50"));
        dto.setPaymentMethod("Mpesa");

        // Validate the DTO
        Set<ConstraintViolation<PaymentRequestDto>> violations = validator.validate(dto);

        // There should be one validation error for the invalid amount
        assertEquals(1, violations.size());

        // Check that the error is specifically about the amount field
        ConstraintViolation<PaymentRequestDto> violation = violations.iterator().next();
        assertEquals("Amount must be greater than or equal to 1", violation.getMessage());
        assertEquals("amount", violation.getPropertyPath().toString());
    }

    @Test
    public void testEmptyPaymentMethodValidation() {
        // Create a PaymentRequestDto with an empty payment method
        PaymentRequestDto dto = new PaymentRequestDto();
        dto.setAmount(new BigDecimal("100.00"));
        dto.setPaymentMethod("");

        // Validate the DTO
        Set<ConstraintViolation<PaymentRequestDto>> violations = validator.validate(dto);

        // There should be one validation error for the empty payment method
        assertEquals(1, violations.size());

        // Check that the error is specifically about the paymentMethod field
        ConstraintViolation<PaymentRequestDto> violation = violations.iterator().next();
        assertEquals("Payment method can not be a null or empty", violation.getMessage());
        assertEquals("paymentMethod", violation.getPropertyPath().toString());
    }

    @Test
    public void testInvalidAccountNumberValidation() {
        // Create a PaymentRequestDto with an invalid account number (not 16 digits)
        PaymentRequestDto dto = new PaymentRequestDto();
        dto.setAmount(new BigDecimal("100.00"));
        dto.setPaymentMethod("Card");
        dto.setAccountNumber("123456");

        // Validate the DTO
        Set<ConstraintViolation<PaymentRequestDto>> violations = validator.validate(dto);

        // There should be one validation error for the invalid account number
        assertEquals(1, violations.size());

        // Check that the error is specifically about the accountNumber field
        ConstraintViolation<PaymentRequestDto> violation = violations.iterator().next();
        assertEquals("AccountNumber must be 16 digits", violation.getMessage());
        assertEquals("accountNumber", violation.getPropertyPath().toString());
    }

    @Test
    public void testAllInvalidFields() {
        // Create a PaymentRequestDto with multiple invalid fields
        PaymentRequestDto dto = new PaymentRequestDto();
        dto.setAmount(new BigDecimal("0.50"));  // Invalid amount (less than 1.0)
        dto.setPaymentMethod("");  // Empty payment method
        dto.setAccountNumber("12345");  // Invalid account number

        // Validate the DTO
        Set<ConstraintViolation<PaymentRequestDto>> violations = validator.validate(dto);

        // Expect 3 violations for the amount, payment method, and account number
        assertEquals(3, violations.size());
    }
}
