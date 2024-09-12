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
    void processPayment(PaymentRequestDto paymentRequestDto);

    /**
     * TODO: Return Payment TransactionDto
     * @param id - Input Payment Transaction Id
     * @return Payment Transaction Details based on a given id
     */
    PaymentsResponseDto getPaymentTransactionById(Long id);

    /**
     * @return List of Payment Transactions in paginated form
     */
    public List<PaymentsResponseDto> getAllPaymentTransactions(int page, int size);
}
