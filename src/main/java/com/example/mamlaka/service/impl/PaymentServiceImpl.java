package com.example.mamlaka.service.impl;

import com.example.mamlaka.constants.PaymentsConstants;
import com.example.mamlaka.dto.PaymentRequestDto;
import com.example.mamlaka.dto.PaymentsResponseDto;
import com.example.mamlaka.entity.PaymentTransaction;
import com.example.mamlaka.entity.User;
import com.example.mamlaka.events.PaymentStatusChangeEvent;
import com.example.mamlaka.exception.*;
import com.example.mamlaka.mapper.PaymentsMapper;
import com.example.mamlaka.repository.PaymentTransactionRepository;
import com.example.mamlaka.repository.UserRepository;
import com.example.mamlaka.security.tokenization.TokenizationService;
import com.example.mamlaka.service.IPaymentService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public PaymentsResponseDto processPayment(PaymentRequestDto paymentRequestDto) throws Exception {
        // Retrieve the logged-in user from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found for this transaction to proceed");
        }
        User user = optionalUser.get();

        PaymentTransaction paymentTransaction = PaymentsMapper.mapToPaymentTransaction(paymentRequestDto, new PaymentTransaction());
        paymentTransaction.setStatus(PaymentsConstants.PENDING);
        paymentTransaction.setTimestamp(new Date());
        paymentTransaction.setUser(user);

        String paymentMethod = paymentRequestDto.getPaymentMethod();
        String transactionId = generateTransactionId();

        // Check for sufficient funds before proceeding
        if (!hasSufficientFunds(paymentRequestDto)) {
            logger.warn("Insufficient funds for user: {} and transactionId: {}", username, transactionId);
            throw new InsufficientFundsException("Insufficient funds for the transaction");
        }

        switch (paymentMethod) {
            case PaymentsConstants.CREDIT_CARD -> processCreditCardPayment(paymentRequestDto, paymentTransaction, transactionId);
            case PaymentsConstants.MPESA -> processMpesaPayment(paymentRequestDto, paymentTransaction, transactionId);
            default -> throw new RuntimeException("Invalid payment method");
        }

        return PaymentsMapper.mapToPaymentsResponseDto(paymentTransaction, new PaymentsResponseDto());
    }

    private String generateTransactionId() {
        // Get the current timestamp in milliseconds
        long timestamp = System.currentTimeMillis();

        // Generate a random 4-digit number
        int randomNumber = ThreadLocalRandom.current().nextInt(1000, 9999);

        // Combine the timestamp with the random number
        // This will improve indexing as still provide the uniqueness of the transaction ID
        return timestamp + String.valueOf(randomNumber);
    }

    // Simulate checking if user has sufficient funds for the transaction. This should technically be done from the payment provider's API
    private boolean hasSufficientFunds(PaymentRequestDto paymentRequestDto) {
        // Assume some logic here to check user's available funds.
        // For now, let's simulate a failure if the amount is greater than a certain threshold
        BigDecimal threshold = new BigDecimal("1000.00"); // Simulated limit
        return paymentRequestDto.getAmount().compareTo(threshold) <= 0;
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
            throw new PaymentProcessingException("Error processing credit card payment", e);
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
            throw new PaymentProcessingException("Error processing MPESA payment", e);
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
        // Define a PageRequest with sorting by 'created_at' in descending order
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return paymentTransactionRepository.findAll(pageRequest).stream()
                .map(paymentTransaction -> PaymentsMapper.mapToPaymentsResponseDto(paymentTransaction, new PaymentsResponseDto()))
                .toList();
    }
}
