package com.example.mamlaka.service.impl;

import com.example.mamlaka.constants.PaymentsConstants;
import com.example.mamlaka.dto.PaymentRequestDto;
import com.example.mamlaka.dto.PaymentsResponseDto;
import com.example.mamlaka.entity.PaymentTransaction;
import com.example.mamlaka.events.PaymentStatusChangeEvent;
import com.example.mamlaka.exception.PhoneNumberRequiredException;
import com.example.mamlaka.exception.RequiredFieldException;
import com.example.mamlaka.exception.ResourceNotFoundException;
import com.example.mamlaka.mapper.PaymentsMapper;
import com.example.mamlaka.repository.PaymentTransactionRepository;
import com.example.mamlaka.security.tokenization.TokenizationService;
import com.example.mamlaka.service.IPaymentService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public PaymentsResponseDto processPayment(PaymentRequestDto paymentRequestDto) throws Exception {
        PaymentTransaction paymentTransaction = PaymentsMapper.mapToPaymentTransaction(paymentRequestDto, new PaymentTransaction());
        paymentTransaction.setStatus(PaymentsConstants.PENDING);
        paymentTransaction.setTimestamp(new Date());

        String paymentMethod = paymentRequestDto.getPaymentMethod();
        String transactionId = generateTransactionId();

        switch (paymentMethod) {
            case PaymentsConstants.CREDIT_CARD -> processCreditCardPayment(paymentRequestDto, paymentTransaction, transactionId);
            case PaymentsConstants.MPESA -> processMpesaPayment(paymentRequestDto, paymentTransaction, transactionId);
            default -> throw new RuntimeException("Invalid payment method");
        }

        return PaymentsMapper.mapToPaymentsResponseDto(paymentTransaction, new PaymentsResponseDto());
    }

    private String generateTransactionId() {
        return String.valueOf(1000000000L + ThreadLocalRandom.current().nextInt(900000000));
    }

    private void processCreditCardPayment(PaymentRequestDto paymentRequestDto, PaymentTransaction paymentTransaction, String transactionId) {
        if (paymentRequestDto.getAccountNumber() == null || paymentRequestDto.getAccountNumber().isEmpty()) {
            throw new RequiredFieldException("Card number", "card payments");
        }
        try {
            String tokenizedCard = TokenizationService.tokenize(paymentRequestDto.getAccountNumber());
            paymentTransaction.setCardNumber(tokenizedCard);
            finalizePayment(paymentTransaction, transactionId);
        } catch (Exception e) {
            throw new RuntimeException("Error processing credit card payment", e);
        }
    }

    private void processMpesaPayment(PaymentRequestDto paymentRequestDto, PaymentTransaction paymentTransaction, String transactionId) {
        if (paymentRequestDto.getPhoneNumber() == null || paymentRequestDto.getPhoneNumber().isEmpty()) {
            throw new PhoneNumberRequiredException("Phone number is required for MPESA payment");
        }
        try {
            String tokenizedPhone = TokenizationService.tokenize(paymentRequestDto.getPhoneNumber());
            paymentTransaction.setPhoneNumber(tokenizedPhone);
            finalizePayment(paymentTransaction, transactionId);
        } catch (Exception e) {
            throw new RuntimeException("Error processing MPESA payment", e);
        }
    }

    private void finalizePayment(PaymentTransaction paymentTransaction, String transactionId) {
        String transactionStatus = ThreadLocalRandom.current().nextBoolean() ? PaymentsConstants.SUCCESS : PaymentsConstants.FAILED;
        paymentTransaction.setStatus(transactionStatus);
        paymentTransaction.setTransactionId(transactionId);
        paymentTransactionRepository.save(paymentTransaction);

        // Trigger webhook if payment is successful
        if (PaymentsConstants.SUCCESS.equals(transactionStatus)) {
//            triggerWebhook(paymentTransaction);
            PaymentsResponseDto responseDto = PaymentsMapper.mapToPaymentsResponseDto(paymentTransaction, new PaymentsResponseDto());
            eventPublisher.publishEvent(new PaymentStatusChangeEvent(responseDto));
        }
    }

    @Override
    @Cacheable(value = "paymentCache", key = "#transactionId")
    public PaymentsResponseDto getPaymentTransactionById(String transactionId) {
        return paymentTransactionRepository.findByTransactionId(transactionId)
                .map(paymentTransaction -> PaymentsMapper.mapToPaymentsResponseDto(paymentTransaction, new PaymentsResponseDto()))
                .orElseThrow(() -> new ResourceNotFoundException("Payment transaction", "id", transactionId));
    }

    @Override
    public List<PaymentsResponseDto> getAllPaymentTransactions(int page, int size) {
        return paymentTransactionRepository.findAll(PageRequest.of(page, size)).stream()
                .map(paymentTransaction -> PaymentsMapper.mapToPaymentsResponseDto(paymentTransaction, new PaymentsResponseDto()))
                .toList();
    }
}
