package com.example.mamlaka.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Schema(
        name = "PaymentsResponse",
        description = "Schema to hold Payment information sent back to client"
)
public class PaymentsResponseDto {

    @Schema(
            description = "Payment amount", example = "1000"
    )
    private BigDecimal amount;

    @Schema(
            description = "Payment status", example = "pending"
    )
    private String status;

    @Schema(
            description = "Payment timestamp", example = ""
    )
    private Date timestamp;

    @Schema(
            description = "Payment method", example = "Mpesa"
    )
    private String paymentMethod;

    @Schema(
            description = "Payment transaction Id", example = "12345"
    )
    private String transactionId;

    @Schema(
            description = "Payment description", example = "Payment for so and so..."
    )
    private String description;
}
