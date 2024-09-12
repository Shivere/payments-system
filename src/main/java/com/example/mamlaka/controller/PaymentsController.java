package com.example.mamlaka.controller;

import com.example.mamlaka.constants.PaymentsConstants;
import com.example.mamlaka.dto.ErrorResponseDto;
import com.example.mamlaka.dto.PaymentRequestDto;
import com.example.mamlaka.dto.PaymentsResponseDto;
import com.example.mamlaka.dto.ResponseDto;
import com.example.mamlaka.entity.IdempotencyRecord;
import com.example.mamlaka.repository.IdempotencyRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author Shivere
 */

@Tag(
        name = "CRUD REST APIs for Payments at Mamlaka",
        description = "CRUD REST APIs at Mamlaka to CREATE AND GET TRANSACTIONS"
)
@RestController
@RequestMapping(path="/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
@Validated
public class PaymentsController {

    private IPaymentService iPaymentsService;
    private final IdempotencyRepository idempotencyRepository;

    @Operation(
            summary = "Process payment REST API",
            description = "REST API to process payment transactions"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @PostMapping("/payment")
    public ResponseEntity<PaymentsResponseDto> processPayment(
            @Valid @RequestBody PaymentRequestDto paymentRequestDto,
            @RequestHeader(value = "Idempotency-Key", required = true) String idempotencyKey) throws Exception {

        // Check if the idempotency key already exists
        Optional<IdempotencyRecord> existingRecord = idempotencyRepository.findByIdempotencyKey(idempotencyKey);

        if (existingRecord.isPresent()) {
            // Return the stored response if this key has been used
            PaymentsResponseDto storedResponse = new ObjectMapper().readValue(existingRecord.get().getResponse(), PaymentsResponseDto.class);
            return ResponseEntity.status(HttpStatus.OK).body(storedResponse);
        }

        // Process the payment if the idempotency key is not found
        PaymentsResponseDto paymentsResponseDto = iPaymentsService.processPayment(paymentRequestDto);

        // Save the idempotency record along with the response
        IdempotencyRecord idempotencyRecord = new IdempotencyRecord();
        idempotencyRecord.setIdempotencyKey(idempotencyKey);
        idempotencyRecord.setTransactionId(paymentsResponseDto.getTransactionId());
        idempotencyRecord.setResponse(new ObjectMapper().writeValueAsString(paymentsResponseDto));

        idempotencyRepository.save(idempotencyRecord);


        boolean paymentTransactionSuccess = PaymentsConstants.SUCCESS.equals(paymentsResponseDto.getStatus());
        System.out.println("Transaction success: " + paymentTransactionSuccess);
        if (paymentTransactionSuccess) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(paymentsResponseDto);
        } else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(paymentsResponseDto);
        }
    }



    @Operation(
            summary = "Payment details REST API",
            description = "REST API to specific payment transaction based on ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @GetMapping("/payment/{id}")
    public ResponseEntity<PaymentsResponseDto> getPaymentInfo(@PathVariable String id) {
        PaymentsResponseDto paymentsResponseDto = iPaymentsService.getPaymentTransactionById(id);
        return ResponseEntity.status(HttpStatus.OK).body(paymentsResponseDto);
    }



    @Operation(
            summary = "Get transactions REST API",
            description = "REST API to get all transactions paginated"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    }
    )
    @GetMapping("/transactions")
    public ResponseEntity<List<PaymentsResponseDto>> getAllTransactions(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        List<PaymentsResponseDto> paymentsResponseDto = iPaymentsService.getAllPaymentTransactions(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(paymentsResponseDto);
    }


}
