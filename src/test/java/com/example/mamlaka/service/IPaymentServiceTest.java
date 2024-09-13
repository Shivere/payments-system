package com.example.mamlaka.service;

import com.example.mamlaka.dto.PaymentRequestDto;
import com.example.mamlaka.dto.PaymentsResponseDto;
import org.junit.jupiter.api.*;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IPaymentServiceTest {

    private IPaymentService paymentService;

    @BeforeEach
    public void setUp() {
        paymentService = mock(IPaymentService.class);
    }

    @Test
    public void testProcessPayment_Success() throws Exception {
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        PaymentsResponseDto paymentsResponseDto = new PaymentsResponseDto();

        when(paymentService.processPayment(any(PaymentRequestDto.class))).thenReturn(paymentsResponseDto);

        PaymentsResponseDto result = paymentService.processPayment(paymentRequestDto);

        assertNotNull(result);
        assertEquals(paymentsResponseDto, result);
        verify(paymentService, times(1)).processPayment(paymentRequestDto);
    }

    @Test
    public void testProcessPayment_Exception() throws Exception {
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();

        when(paymentService.processPayment(any(PaymentRequestDto.class))).thenThrow(new Exception("Processing error"));

        Exception exception = assertThrows(Exception.class, () -> paymentService.processPayment(paymentRequestDto));

        assertEquals("Processing error", exception.getMessage());
        verify(paymentService, times(1)).processPayment(paymentRequestDto);
    }

    @Test
    public void testGetPaymentTransactionById_Success() {
        String transactionId = "transaction-id";
        PaymentsResponseDto paymentsResponseDto = new PaymentsResponseDto();

        when(paymentService.getPaymentTransactionById(transactionId)).thenReturn(paymentsResponseDto);

        PaymentsResponseDto result = paymentService.getPaymentTransactionById(transactionId);

        assertNotNull(result);
        assertEquals(paymentsResponseDto, result);
        verify(paymentService, times(1)).getPaymentTransactionById(transactionId);
    }

    @Test
    public void testGetPaymentTransactionById_NotFound() {
        String transactionId = "non-existent-id";

        when(paymentService.getPaymentTransactionById(transactionId)).thenReturn(null);

        PaymentsResponseDto result = paymentService.getPaymentTransactionById(transactionId);

        assertNull(result);
        verify(paymentService, times(1)).getPaymentTransactionById(transactionId);
    }

    @Test
    public void testGetAllPaymentTransactions_Success() {
        int page = 0;
        int size = 10;
        PaymentsResponseDto paymentsResponseDto = new PaymentsResponseDto();
        List<PaymentsResponseDto> responseDtos = Collections.singletonList(paymentsResponseDto);

        when(paymentService.getAllPaymentTransactions(page, size)).thenReturn(responseDtos);

        List<PaymentsResponseDto> result = paymentService.getAllPaymentTransactions(page, size);

        assertNotNull(result);
        assertEquals(responseDtos, result);
        verify(paymentService, times(1)).getAllPaymentTransactions(page, size);
    }

    @Test
    public void testGetAllPaymentTransactions_Empty() {
        int page = 0;
        int size = 10;

        when(paymentService.getAllPaymentTransactions(page, size)).thenReturn(Collections.emptyList());

        List<PaymentsResponseDto> result = paymentService.getAllPaymentTransactions(page, size);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(paymentService, times(1)).getAllPaymentTransactions(page, size);
    }

    @AfterEach
    public void tearDown() {
        reset(paymentService);
    }
}
