package com.example.mamlaka.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PaymentsResponseDtoTest {

    private PaymentsResponseDto paymentsResponseDto;

    @BeforeEach
    public void setUp() {
        // Initialize a valid PaymentsResponseDto instance
        paymentsResponseDto = new PaymentsResponseDto();
    }

    @Test
    public void testSetAndGetAmount() {
        BigDecimal expectedAmount = new BigDecimal("1000.00");
        paymentsResponseDto.setAmount(expectedAmount);

        // Assert that the amount was set and retrieved correctly
        assertEquals(expectedAmount, paymentsResponseDto.getAmount());
    }

    @Test
    public void testSetAndGetStatus() {
        String expectedStatus = "pending";
        paymentsResponseDto.setStatus(expectedStatus);

        // Assert that the status was set and retrieved correctly
        assertEquals(expectedStatus, paymentsResponseDto.getStatus());
    }

    @Test
    public void testSetAndGetTimestamp() {
        Date expectedTimestamp = new Date();
        paymentsResponseDto.setTimestamp(expectedTimestamp);

        // Assert that the timestamp was set and retrieved correctly
        assertEquals(expectedTimestamp, paymentsResponseDto.getTimestamp());
    }

    @Test
    public void testSetAndGetPaymentMethod() {
        String expectedPaymentMethod = "Mpesa";
        paymentsResponseDto.setPaymentMethod(expectedPaymentMethod);

        // Assert that the payment method was set and retrieved correctly
        assertEquals(expectedPaymentMethod, paymentsResponseDto.getPaymentMethod());
    }

    @Test
    public void testSetAndGetTransactionId() {
        String expectedTransactionId = "12345";
        paymentsResponseDto.setTransactionId(expectedTransactionId);

        // Assert that the transaction ID was set and retrieved correctly
        assertEquals(expectedTransactionId, paymentsResponseDto.getTransactionId());
    }

    @Test
    public void testSetAndGetDescription() {
        String expectedDescription = "Payment for so and so...";
        paymentsResponseDto.setDescription(expectedDescription);

        // Assert that the description was set and retrieved correctly
        assertEquals(expectedDescription, paymentsResponseDto.getDescription());
    }

    @Test
    public void testAllFieldsTogether() {
        BigDecimal expectedAmount = new BigDecimal("1000.00");
        String expectedStatus = "completed";
        Date expectedTimestamp = new Date();
        String expectedPaymentMethod = "Mpesa";
        String expectedTransactionId = "54321";
        String expectedDescription = "Payment for services";

        // Set all fields
        paymentsResponseDto.setAmount(expectedAmount);
        paymentsResponseDto.setStatus(expectedStatus);
        paymentsResponseDto.setTimestamp(expectedTimestamp);
        paymentsResponseDto.setPaymentMethod(expectedPaymentMethod);
        paymentsResponseDto.setTransactionId(expectedTransactionId);
        paymentsResponseDto.setDescription(expectedDescription);

        // Assert all fields were set and retrieved correctly
        assertEquals(expectedAmount, paymentsResponseDto.getAmount());
        assertEquals(expectedStatus, paymentsResponseDto.getStatus());
        assertEquals(expectedTimestamp, paymentsResponseDto.getTimestamp());
        assertEquals(expectedPaymentMethod, paymentsResponseDto.getPaymentMethod());
        assertEquals(expectedTransactionId, paymentsResponseDto.getTransactionId());
        assertEquals(expectedDescription, paymentsResponseDto.getDescription());
    }
}
