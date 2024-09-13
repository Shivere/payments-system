package com.example.mamlaka.controller;

import com.example.mamlaka.constants.PaymentsConstants;
import com.example.mamlaka.dto.ErrorResponseDto;
import com.example.mamlaka.dto.PaymentRequestDto;
import com.example.mamlaka.dto.PaymentsResponseDto;
import com.example.mamlaka.entity.IdempotencyRecord;
import com.example.mamlaka.repository.IdempotencyRepository;
import com.example.mamlaka.service.IIdempotencyService;
import com.example.mamlaka.service.IPaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * CRUD REST APIs for Payments at Mamlaka
 */
@Tag(name = "CRUD REST APIs for Payments", description = "CRUD REST APIs to CREATE AND GET TRANSACTIONS")
@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
@Validated
public class PaymentsController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentsController.class);

    private final IPaymentService iPaymentsService;
    private final IIdempotencyService idempotencyService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "Process payment REST API", description = "REST API to process payment transactions")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/payment")
    public ResponseEntity<PaymentsResponseDto> processPayment(
            @Valid @RequestBody PaymentRequestDto paymentRequestDto,
            @RequestHeader(value = "Idempotency-Key", required = true) String idempotencyKey) throws Exception {

        // Check if the idempotency key already exists
        Optional<IdempotencyRecord> existingRecord = idempotencyService.getIdempotencyRecord(idempotencyKey);

        if (existingRecord.isPresent()) {
            // Return the stored response if the key has been used
            PaymentsResponseDto storedResponse = objectMapper.readValue(existingRecord.get().getResponse(), PaymentsResponseDto.class);
            return ResponseEntity.ok(storedResponse);
        }

        // Process the payment if the idempotency key is not found
        PaymentsResponseDto paymentsResponseDto = iPaymentsService.processPayment(paymentRequestDto);

        // Save the idempotency record along with the response
        idempotencyService.saveIdempotencyRecord(idempotencyKey, paymentsResponseDto);

        // Log transaction success
        boolean isSuccess = PaymentsConstants.SUCCESS.equals(paymentsResponseDto.getStatus());
        logger.info("Transaction success: {}", isSuccess);

        // Simplified return based on success or failure
        return ResponseEntity
                .status(isSuccess ? HttpStatus.OK : HttpStatus.EXPECTATION_FAILED)
                .body(paymentsResponseDto);
    }

    @Operation(summary = "Payment details REST API", description = "REST API to get specific payment transaction by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/payment/{id}")
    public ResponseEntity<PaymentsResponseDto> getPaymentInfo(@PathVariable String id) {
        PaymentsResponseDto paymentsResponseDto = iPaymentsService.getPaymentTransactionById(id);
        return ResponseEntity.ok(paymentsResponseDto);
    }

    @Operation(summary = "Get transactions REST API", description = "REST API to get all transactions paginated")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "HTTP Status OK"),
            @ApiResponse(responseCode = "500", description = "HTTP Status Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/transactions")
    public ResponseEntity<List<PaymentsResponseDto>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<PaymentsResponseDto> transactions = iPaymentsService.getAllPaymentTransactions(page, size);
        return ResponseEntity.ok(transactions);
    }
}
