package com.example.mamlaka.mapper;

import com.example.mamlaka.dto.PaymentRequestDto;
import com.example.mamlaka.dto.PaymentsResponseDto;
import com.example.mamlaka.entity.PaymentTransaction;

public class PaymentsMapper {

    public static PaymentTransaction mapToPaymentTransaction(PaymentRequestDto paymentRequestDto, PaymentTransaction paymentTransaction) {
        paymentTransaction.setAmount(paymentRequestDto.getAmount());
        paymentTransaction.setDescription(paymentRequestDto.getDescription());
        paymentTransaction.setPaymentMethod(paymentRequestDto.getPaymentMethod());
        return paymentTransaction;
    }

    public static PaymentsResponseDto mapToPaymentsResponseDto(PaymentTransaction paymentTransaction, PaymentsResponseDto paymentsResponseDto) {
        paymentsResponseDto.setAmount(paymentTransaction.getAmount());
        paymentsResponseDto.setStatus(paymentTransaction.getStatus());
        paymentsResponseDto.setTimestamp(paymentTransaction.getTimestamp());
        paymentsResponseDto.setPaymentMethod(paymentTransaction.getPaymentMethod());
        paymentsResponseDto.setDescription(paymentTransaction.getDescription());
        return paymentsResponseDto;
    }
}
