package com.example.mamlaka.controller;

import com.example.mamlaka.constants.PaymentsConstants;
import com.example.mamlaka.dto.ErrorResponseDto;
import com.example.mamlaka.dto.PaymentRequestDto;
import com.example.mamlaka.dto.PaymentsResponseDto;
import com.example.mamlaka.dto.ResponseDto;
import com.example.mamlaka.service.IPaymentService;
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
    public ResponseEntity<PaymentsResponseDto> processPayment(@Valid @RequestBody PaymentRequestDto paymentRequestDto) throws Exception {
        PaymentsResponseDto paymentsResponseDto = iPaymentsService.processPayment(paymentRequestDto);
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
