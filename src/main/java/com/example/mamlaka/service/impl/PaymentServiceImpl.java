package com.example.mamlaka.service.impl;

import com.example.mamlaka.constants.PaymentsConstants;
import com.example.mamlaka.dto.PaymentRequestDto;
import com.example.mamlaka.dto.PaymentsResponseDto;
import com.example.mamlaka.entity.PaymentTransaction;
import com.example.mamlaka.exception.PhoneNumberRequiredException;
import com.example.mamlaka.exception.RequiredFieldException;
import com.example.mamlaka.exception.ResourceNotFoundException;
import com.example.mamlaka.mapper.PaymentsMapper;
import com.example.mamlaka.repository.PaymentTransactionRepository;
import com.example.mamlaka.security.tokenization.TokenizationService;
import com.example.mamlaka.service.IPaymentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private final PaymentTransactionRepository paymentTransactionRepository;

    @Override
    public PaymentsResponseDto processPayment(PaymentRequestDto paymentRequestDto) throws Exception {
        PaymentsResponseDto paymentsResponseDto = new PaymentsResponseDto();
        PaymentTransaction paymentTransaction = PaymentsMapper.mapToPaymentTransaction(paymentRequestDto, new PaymentTransaction());
        paymentTransaction.setStatus(PaymentsConstants.PENDING);
        paymentTransaction.setTimestamp(LocalDateTime.now());

        if ("CREDIT_CARD".equals(paymentRequestDto.getPaymentMethod())) {
            if (paymentRequestDto.getAccountNumber() == null || paymentRequestDto.getAccountNumber().isEmpty()) {
                throw new RequiredFieldException("Card number", "card payments");
            }
            try {
                // Tokenize card number;
                String tokenizedPaymentDetails = TokenizationService.tokenize(paymentRequestDto.getAccountNumber());

                // Simulate the payment processing result (random success or failure)
                String transactionStatus = (new Random().nextBoolean()) ? PaymentsConstants.SUCCESS : PaymentsConstants.FAILED;
                // String transactionId = UUID.randomUUID().toString();
                String transactionId = String.valueOf(1000000000L + new Random().nextInt(900000000));

                paymentTransaction.setStatus(transactionStatus);
                paymentTransaction.setTransactionId(transactionId);
                paymentTransaction.setCardNumber(tokenizedPaymentDetails);
                paymentTransactionRepository.save(paymentTransaction);

                paymentsResponseDto = PaymentsMapper.mapToPaymentsResponseDto(paymentTransaction, new PaymentsResponseDto());
            } catch (Exception e) {
                throw new RuntimeException("Error processing payment", e);
            }
        } else if ("MPESA".equals(paymentRequestDto.getPaymentMethod())) {
            if (paymentRequestDto.getPhoneNumber() == null || paymentRequestDto.getPhoneNumber().isEmpty()) {
                throw new PhoneNumberRequiredException("Phone number is required for MPESA payment");
            }
            try {
                // Tokenize phone number;
                String tokenizedPaymentDetails = TokenizationService.tokenize(paymentRequestDto.getPhoneNumber());

                // Simulate the payment processing result (random success or failure)
                String transactionStatus = (new Random().nextBoolean()) ? "SUCCESS" : "FAILED";
                // String transactionId = UUID.randomUUID().toString();
                String transactionId = String.valueOf(1000000000L + new Random().nextInt(900000000));

                paymentTransaction.setStatus(transactionStatus);
                paymentTransaction.setTransactionId(transactionId);
                paymentTransaction.setPhoneNumber(tokenizedPaymentDetails);
                paymentTransactionRepository.save(paymentTransaction);
                paymentsResponseDto = PaymentsMapper.mapToPaymentsResponseDto(paymentTransaction, new PaymentsResponseDto());
            } catch (Exception e) {
                throw new RuntimeException("Error processing payment", e);
            }
        } else {
            throw new RuntimeException("Invalid payment method");
        }
        return paymentsResponseDto;
    }

    @Override
    public PaymentsResponseDto getPaymentTransactionById(String transactionId) {
        PaymentTransaction paymentTransaction = paymentTransactionRepository.findByTransactionId(transactionId).orElseThrow(
                () -> new ResourceNotFoundException("Payment transaction", "id", transactionId.toString())
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
