package com.example.mamlaka.events;


import com.example.mamlaka.dto.PaymentsResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentStatusChangeEvent {
    private PaymentsResponseDto paymentResponse;
}
