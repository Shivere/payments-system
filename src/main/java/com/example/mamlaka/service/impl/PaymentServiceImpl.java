package com.example.mamlaka.service.impl;

import com.example.mamlaka.dto.PaymentRequestDto;
import com.example.mamlaka.dto.PaymentsResponseDto;
import com.example.mamlaka.entity.PaymentTransaction;
import com.example.mamlaka.exception.ResourceNotFoundException;
import com.example.mamlaka.mapper.PaymentsMapper;
import com.example.mamlaka.repository.PaymentTransactionRepository;
import com.example.mamlaka.service.IPaymentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private final PaymentTransactionRepository paymentTransactionRepository;

    @Override
    public void processPayment(PaymentRequestDto paymentRequestDto) {
        PaymentTransaction paymentTransaction = PaymentsMapper.mapToPaymentTransaction(paymentRequestDto, new PaymentTransaction());
        paymentTransaction.setStatus("PENDING");
        paymentTransaction.setTimestamp(LocalDateTime.now());
        paymentTransactionRepository.save(paymentTransaction);
    }

    @Override
    public PaymentsResponseDto getPaymentTransactionById(Long id) {
        PaymentTransaction paymentTransaction = paymentTransactionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Payment transaction", "id", id.toString())
        );
        PaymentsResponseDto paymentsResponseDto =  PaymentsMapper.mapToPaymentsResponseDto(paymentTransaction, new PaymentsResponseDto());

        return paymentsResponseDto;
    }

    @Override
    public List<PaymentsResponseDto> getAllPaymentTransactions(int page, int size) {
        List<PaymentTransaction> paymentTransactions = paymentTransactionRepository.findAll(PageRequest.of(page, size)).getContent();

        // Map PaymentTransaction to PaymentsResponseDto
        return paymentTransactions.stream()
                .map(paymentTransaction -> PaymentsMapper.mapToPaymentsResponseDto(paymentTransaction, new PaymentsResponseDto())).toList();
    }
}
