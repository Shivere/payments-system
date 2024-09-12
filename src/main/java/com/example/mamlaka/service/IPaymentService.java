package com.example.mamlaka.service;

import com.example.mamlaka.dto.PaymentRequestDto;
import com.example.mamlaka.dto.PaymentsResponseDto;
import com.example.mamlaka.entity.PaymentTransaction;

import java.util.List;
import java.util.Optional;

public interface IPaymentService {

    /**
     *
     * @param paymentRequestDto - PaymentRequestDto Object
     */
    PaymentsResponseDto processPayment(PaymentRequestDto paymentRequestDto) throws Exception;

    /**
     * @param transactionId - Input Payment Transaction Id
     * @return Payment Transaction Details based on a given id
     */
    PaymentsResponseDto getPaymentTransactionById(String transactionId);

    /**
     * @return List of Payment Transactions in paginated form
     */
    public List<PaymentsResponseDto> getAllPaymentTransactions(int page, int size);
}
