package com.example.mamlaka.controller;

import com.example.mamlaka.constants.PaymentsConstants;
import com.example.mamlaka.dto.PaymentRequestDto;
import com.example.mamlaka.dto.PaymentsResponseDto;
import com.example.mamlaka.entity.IdempotencyRecord;
import com.example.mamlaka.service.IIdempotencyService;
import com.example.mamlaka.service.IPaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


public class PaymentsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IPaymentService iPaymentsService;

    @Mock
    private IIdempotencyService idempotencyService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PaymentsController paymentsController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(paymentsController).build();
    }

    @Test
    public void testProcessPayment_SuccessNewTransaction() throws Exception {
        PaymentRequestDto requestDto = new PaymentRequestDto();
        String idempotencyKey = "unique-key";

        PaymentsResponseDto responseDto = new PaymentsResponseDto();
        responseDto.setStatus(PaymentsConstants.SUCCESS);

        when(idempotencyService.getIdempotencyRecord(idempotencyKey)).thenReturn(Optional.empty());
        when(iPaymentsService.processPayment(any(PaymentRequestDto.class))).thenReturn(responseDto);

        ResponseEntity<PaymentsResponseDto> response = paymentsController.processPayment(requestDto, idempotencyKey);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
    }

    @Test
    public void testProcessPayment_SuccessExistingTransaction() throws Exception {
        PaymentRequestDto requestDto = new PaymentRequestDto();
        String idempotencyKey = "unique-key";

        IdempotencyRecord existingRecord = new IdempotencyRecord();
        existingRecord.setResponse("serialized-response");

        PaymentsResponseDto storedResponse = new PaymentsResponseDto();
        storedResponse.setStatus(PaymentsConstants.SUCCESS);

        when(idempotencyService.getIdempotencyRecord(idempotencyKey)).thenReturn(Optional.of(existingRecord));
        when(objectMapper.readValue(existingRecord.getResponse(), PaymentsResponseDto.class)).thenReturn(storedResponse);

        ResponseEntity<PaymentsResponseDto> response = paymentsController.processPayment(requestDto, idempotencyKey);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(storedResponse, response.getBody());
    }

    @Test
    public void testProcessPayment_ValidationError() throws Exception {
        // Create an invalid PaymentRequestDto object (missing required fields)
        PaymentRequestDto requestDto = new PaymentRequestDto();
        String idempotencyKey = "unique-key";

        // Perform the HTTP POST request using MockMvc
        mockMvc.perform(post("/api/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Idempotency-Key", idempotencyKey)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))  // Empty or invalid request body
                .andExpect(status().isBadRequest())  // Expect a 400 Bad Request due to validation failure
                .andDo(print());
    }

    @Test
    public void testGetPaymentInfo_Success() {
        String transactionId = "transaction-id";

        PaymentsResponseDto responseDto = new PaymentsResponseDto();
        responseDto.setStatus(PaymentsConstants.SUCCESS);

        when(iPaymentsService.getPaymentTransactionById(transactionId)).thenReturn(responseDto);

        ResponseEntity<PaymentsResponseDto> response = paymentsController.getPaymentInfo(transactionId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
    }

    @Test
    public void testGetAllTransactions_Success() {
        int page = 0;
        int size = 10;

        PaymentsResponseDto responseDto = new PaymentsResponseDto();
        responseDto.setStatus(PaymentsConstants.SUCCESS);

        List<PaymentsResponseDto> transactions = Collections.singletonList(responseDto);

        when(iPaymentsService.getAllPaymentTransactions(page, size)).thenReturn(transactions);

        ResponseEntity<List<PaymentsResponseDto>> response = paymentsController.getAllTransactions(page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactions, response.getBody());
    }
}
