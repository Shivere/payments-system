package com.example.mamlaka.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(
        name = "Payment",
        description = "Schema to hold Payment information"
)
public class PaymentRequestDto {

    @Schema(
            description = "Payment Amount", example = "1000"
    )
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.0", inclusive = true, message = "Amount must be greater than or equal to 1")
    private BigDecimal amount;

    @Schema(
            description = "Payment method selected by user", example = "Mpesa"
    )
    @NotEmpty(message = "Payment method can not be a null or empty")
    private String paymentMethod;

    @Schema(
            description = "Payment phone number", example = "0700000000"
    )
    // TODO: Add custom phone number validation if payment method is MPESA. Currently throwing runtime errors.
    private String phoneNumber;

    @Schema(
            description = "Payment card number", example = "123453"
    )
    @Pattern(regexp="(^$|[0-9]{16})",message = "AccountNumber must be 16 digits")
    // TODO: Add custom account number validation if payment method is CARD. Currently throwing runtime errors.
    private String accountNumber;

    @Schema(
            description = "Payment description", example = "Payment for ..."
    )
    private String description;

}
