package com.example.mamlaka.service.impl;

import com.example.mamlaka.constants.PaymentsConstants;
import com.example.mamlaka.dto.PaymentRequestDto;
import com.example.mamlaka.dto.PaymentsResponseDto;
import com.example.mamlaka.entity.PaymentTransaction;
import com.example.mamlaka.entity.User;
import com.example.mamlaka.events.PaymentStatusChangeEvent;
import com.example.mamlaka.exception.*;
import com.example.mamlaka.repository.PaymentTransactionRepository;
import com.example.mamlaka.repository.UserRepository;
import com.example.mamlaka.security.tokenization.TokenizationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceImplTest {

    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        // Initialize mock objects and set the security context to simulate a logged-in user
        MockitoAnnotations.openMocks(this);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

//    @Test
//    void testProcessPayment_SuccessCreditCard() throws Exception {
//        // Arrange: Create a PaymentRequestDto for a credit card payment
//        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
//        paymentRequestDto.setPaymentMethod(PaymentsConstants.CREDIT_CARD);
//        paymentRequestDto.setAccountNumber("1234567890123456");
//        paymentRequestDto.setAmount(new BigDecimal("500.00"));
//
//        User user = new User();
//        user.setUsername("testuser");
//
//        PaymentTransaction paymentTransaction = new PaymentTransaction();
//        paymentTransaction.setTransactionId("txn1234");
//        paymentTransaction.setStatus(PaymentsConstants.SUCCESS);
//
//        // Mocking the necessary repository and service calls
//        when(authentication.getName()).thenReturn("testuser");
//        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
//        when(paymentTransactionRepository.save(any(PaymentTransaction.class))).thenReturn(paymentTransaction);
//        when(TokenizationService.tokenize(anyString())).thenReturn("tokenized");
//
//        // Act: Call the processPayment method
//        PaymentsResponseDto result = paymentService.processPayment(paymentRequestDto);
//
//        // Assert: Validate that the payment was processed successfully
//        assertNotNull(result);
//        assertEquals(PaymentsConstants.SUCCESS, result.getStatus());
//        verify(paymentTransactionRepository, times(1)).save(any(PaymentTransaction.class));
//    }

    @Test
    void testProcessPayment_InsufficientFunds() {
        // Arrange: Create a PaymentRequestDto for a credit card payment exceeding available balance
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setPaymentMethod(PaymentsConstants.CREDIT_CARD);
        paymentRequestDto.setAmount(new BigDecimal("1500.00"));  // Amount exceeds the limit

        User user = new User();
        user.setUsername("testuser");

        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act & Assert: Expect an InsufficientFundsException when processing the payment
        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class, () -> {
            paymentService.processPayment(paymentRequestDto);
        });

        // Assert: Check the exception message and ensure no transaction was saved
        assertEquals("Insufficient funds for the transaction", exception.getMessage());
        verify(paymentTransactionRepository, times(0)).save(any(PaymentTransaction.class));
    }

//    @Test
//    void testProcessPayment_InvalidPaymentMethod() {
//        // Arrange: Create a PaymentRequestDto with an invalid payment method
//        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
//        paymentRequestDto.setPaymentMethod("INVALID_METHOD");
//
//        User user = new User();
//        user.setUsername("testuser");
//
//        when(authentication.getName()).thenReturn("testuser");
//        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
//
//        // Act & Assert: Expect a RuntimeException when an invalid payment method is used
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            paymentService.processPayment(paymentRequestDto);
//        });
//
//        // Assert: Verify the exception message and ensure no transaction was saved
//        assertEquals("Invalid payment method", exception.getMessage());
//        verify(paymentTransactionRepository, times(0)).save(any(PaymentTransaction.class));
//    }

//    @Test
//    void testProcessPayment_UserNotFound() {
//        // Arrange: Create a PaymentRequestDto for a valid payment
//        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
//        paymentRequestDto.setPaymentMethod(PaymentsConstants.CREDIT_CARD);
//        paymentRequestDto.setAmount(new BigDecimal("500.00"));
//
//        when(authentication.getName()).thenReturn("unknownuser");
//        when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());
//
//        // Act & Assert: Expect a RuntimeException when the user is not found
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            paymentService.processPayment(paymentRequestDto);
//        });
//
//        // Assert: Verify the exception message and ensure no transaction was saved
//        assertEquals("User not found for this transaction to proceed", exception.getMessage());
//        verify(paymentTransactionRepository, times(0)).save(any(PaymentTransaction.class));
//    }

    @Test
    void testGetPaymentTransactionById_Success() {
        // Arrange: Simulate a payment transaction that exists in the database
        String transactionId = "txn1234";
        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setTransactionId(transactionId);

        // Mock the repository to return the transaction
        when(paymentTransactionRepository.findByTransactionId(transactionId)).thenReturn(Optional.of(paymentTransaction));

        // Act: Call getPaymentTransactionById
        PaymentsResponseDto result = paymentService.getPaymentTransactionById(transactionId);

        // Assert: Ensure the result matches the expected transaction
        assertNotNull(result);
        assertEquals(transactionId, result.getTransactionId());
        verify(paymentTransactionRepository, times(1)).findByTransactionId(transactionId);
    }

    @Test
    void testGetPaymentTransactionById_NotFound() {
        // Arrange: Simulate that no payment transaction exists with the given ID
        String transactionId = "txn1234";

        when(paymentTransactionRepository.findByTransactionId(transactionId)).thenReturn(Optional.empty());

        // Act & Assert: Expect a ResourceNotFoundException when the transaction is not found
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            paymentService.getPaymentTransactionById(transactionId);
        });

        // Assert: Verify the exception message and that the repository was called
        assertEquals("Payment transaction not found with the given input data id : 'txn1234'", exception.getMessage());
        verify(paymentTransactionRepository, times(1)).findByTransactionId(transactionId);
    }


    @Test
    void testGetAllPaymentTransactions_Success() {
        // Arrange: Simulate a paginated list of payment transactions
        int page = 0;
        int size = 10;
        PaymentTransaction paymentTransaction = new PaymentTransaction();
        List<PaymentTransaction> transactions = Arrays.asList(paymentTransaction);
        Page<PaymentTransaction> pageTransactions = new PageImpl<>(transactions);

        // Mock the repository to return the paginated result
        when(paymentTransactionRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))).thenReturn(pageTransactions);

        // Act: Call getAllPaymentTransactions
        List<PaymentsResponseDto> result = paymentService.getAllPaymentTransactions(page, size);

        // Assert: Ensure the result contains the correct number of transactions
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(paymentTransactionRepository, times(1)).findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @Test
    void testGetAllPaymentTransactions_Empty() {
        // Arrange: Simulate an empty paginated list of payment transactions
        int page = 0;
        int size = 10;
        List<PaymentTransaction> transactions = Collections.emptyList();
        Page<PaymentTransaction> pageTransactions = new PageImpl<>(transactions);

        // Mock the repository to return an empty result
        when(paymentTransactionRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))).thenReturn(pageTransactions);

        // Act: Call getAllPaymentTransactions
        List<PaymentsResponseDto> result = paymentService.getAllPaymentTransactions(page, size);

        // Assert: Ensure the result is empty
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(paymentTransactionRepository, times(1)).findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @Test
    void testProcessPayment_PhoneNumberRequiredForMpesa() {
        // Arrange: Create a PaymentRequestDto for an Mpesa payment without a phone number
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setPaymentMethod(PaymentsConstants.MPESA);
        paymentRequestDto.setAmount(new BigDecimal("500.00"));

        // Simulate a user entity
        User user = new User();
        user.setUsername("testuser");

        // Mock necessary calls
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act & Assert: Expect a PhoneNumberRequiredException when no phone number is provided
        PhoneNumberRequiredException exception = assertThrows(PhoneNumberRequiredException.class, () -> {
            paymentService.processPayment(paymentRequestDto);
        });

        // Assert: Check the exception message and ensure no transaction was saved
        assertEquals("Phone number is required for MPESA payment", exception.getMessage());
        verify(paymentTransactionRepository, times(0)).save(any(PaymentTransaction.class));
    }

//    @Test
//    void testProcessPayment_FinalizePayment_Success_Mpesa() throws Exception {
//        // Arrange: Simulate a successful Mpesa payment
//        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
//        paymentRequestDto.setPaymentMethod(PaymentsConstants.MPESA);
//        paymentRequestDto.setPhoneNumber("0700123456");
//        paymentRequestDto.setAmount(new BigDecimal("500.00"));
//
//        User user = new User();
//        user.setUsername("testuser");
//
//        PaymentTransaction paymentTransaction = new PaymentTransaction();
//        paymentTransaction.setTransactionId("txn1234");
//        paymentTransaction.setStatus(PaymentsConstants.SUCCESS);
//
//        // Mock necessary calls
//        when(authentication.getName()).thenReturn("testuser");
//        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
//        when(paymentTransactionRepository.save(any(PaymentTransaction.class))).thenReturn(paymentTransaction);
//        when(TokenizationService.tokenize(anyString())).thenReturn("tokenized");
//
//        // Act: Process the payment and finalize
//        PaymentsResponseDto result = paymentService.processPayment(paymentRequestDto);
//
//        // Assert: Validate success and that an event was published
//        assertNotNull(result);
//        assertEquals(PaymentsConstants.SUCCESS, result.getStatus());
//        verify(paymentTransactionRepository, times(1)).save(any(PaymentTransaction.class));
//        verify(eventPublisher, times(1)).publishEvent(any(PaymentStatusChangeEvent.class));
//    }

//    @Test
//    void testProcessPayment_FinalizePayment_Failed_Mpesa() throws Exception {
//        // Arrange: Simulate a failed Mpesa payment
//        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
//        paymentRequestDto.setPaymentMethod(PaymentsConstants.MPESA);
//        paymentRequestDto.setPhoneNumber("0700123456");
//        paymentRequestDto.setAmount(new BigDecimal("500.00"));
//
//        User user = new User();
//        user.setUsername("testuser");
//
//        PaymentTransaction paymentTransaction = new PaymentTransaction();
//        paymentTransaction.setTransactionId("txn1234");
//        paymentTransaction.setStatus(PaymentsConstants.FAILED);
//
//        // Mock necessary calls
//        when(authentication.getName()).thenReturn("testuser");
//        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
//        when(paymentTransactionRepository.save(any(PaymentTransaction.class))).thenReturn(paymentTransaction);
//        when(TokenizationService.tokenize(anyString())).thenReturn("tokenized");
//
//        // Act: Process the payment and finalize
//        PaymentsResponseDto result = paymentService.processPayment(paymentRequestDto);
//
//        // Assert: Validate failure and ensure no event was published
//        assertNotNull(result);
//        assertEquals(PaymentsConstants.FAILED, result.getStatus());
//        verify(paymentTransactionRepository, times(1)).save(any(PaymentTransaction.class));
//        verify(eventPublisher, times(0)).publishEvent(any(PaymentStatusChangeEvent.class));
//    }

    @Test
    void testProcessPayment_RequiredFieldExceptionForCreditCard() {
        // Arrange: Create a PaymentRequestDto for a credit card payment without card number
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setPaymentMethod(PaymentsConstants.CREDIT_CARD);
        paymentRequestDto.setAmount(new BigDecimal("500.00"));

        User user = new User();
        user.setUsername("testuser");

        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act & Assert: Expect a RequiredFieldException when no card number is provided
        RequiredFieldException exception = assertThrows(RequiredFieldException.class, () -> {
            paymentService.processPayment(paymentRequestDto);
        });

        // Assert: Verify the exception message and ensure no transaction was saved
        assertEquals("Card number is required for card payments", exception.getMessage());
        verify(paymentTransactionRepository, times(0)).save(any(PaymentTransaction.class));
    }

//    @Test
//    void testProcessPayment_FinalizePayment_Success_CreditCard() throws Exception {
//        // Arrange: Simulate a successful credit card payment
//        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
//        paymentRequestDto.setPaymentMethod(PaymentsConstants.CREDIT_CARD);
//        paymentRequestDto.setAccountNumber("1234567890123456");
//        paymentRequestDto.setAmount(new BigDecimal("500.00"));
//
//        User user = new User();
//        user.setUsername("testuser");
//
//        PaymentTransaction paymentTransaction = new PaymentTransaction();
//        paymentTransaction.setTransactionId("txn1234");
//        paymentTransaction.setStatus(PaymentsConstants.SUCCESS);
//
//        // Mock necessary calls
//        when(authentication.getName()).thenReturn("testuser");
//        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
//        when(paymentTransactionRepository.save(any(PaymentTransaction.class))).thenReturn(paymentTransaction);
//        when(TokenizationService.tokenize(anyString())).thenReturn("tokenized");
//
//        // Act: Process the payment and finalize
//        PaymentsResponseDto result = paymentService.processPayment(paymentRequestDto);
//
//        // Assert: Validate success and that an event was published
//        assertNotNull(result);
//        assertEquals(PaymentsConstants.SUCCESS, result.getStatus());
//        verify(paymentTransactionRepository, times(1)).save(any(PaymentTransaction.class));
//        verify(eventPublisher, times(1)).publishEvent(any(PaymentStatusChangeEvent.class));
//    }

//    @Test
//    void testProcessPayment_FinalizePayment_Failed_CreditCard() throws Exception {
//        // Arrange: Simulate a failed credit card payment
//        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
//        paymentRequestDto.setPaymentMethod(PaymentsConstants.CREDIT_CARD);
//        paymentRequestDto.setAccountNumber("1234567890123456");
//        paymentRequestDto.setAmount(new BigDecimal("500.00"));
//
//        User user = new User();
//        user.setUsername("testuser");
//
//        PaymentTransaction paymentTransaction = new PaymentTransaction();
//        paymentTransaction.setTransactionId("txn1234");
//        paymentTransaction.setStatus(PaymentsConstants.FAILED);
//
//        // Mock necessary calls
//        when(authentication.getName()).thenReturn("testuser");
//        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
//        when(paymentTransactionRepository.save(any(PaymentTransaction.class))).thenReturn(paymentTransaction);
//        when(TokenizationService.tokenize(anyString())).thenReturn("tokenized");
//
//        // Act: Process the payment and finalize
//        PaymentsResponseDto result = paymentService.processPayment(paymentRequestDto);
//
//        // Assert: Validate failure and ensure no event was published
//        assertNotNull(result);
//        assertEquals(PaymentsConstants.FAILED, result.getStatus());
//        verify(paymentTransactionRepository, times(1)).save(any(PaymentTransaction.class));
//        verify(eventPublisher, times(0)).publishEvent(any(PaymentStatusChangeEvent.class));
//    }

    @AfterEach
    void tearDown() {
        // Reset mocks after each test to ensure isolation between tests
        reset(paymentTransactionRepository, userRepository, securityContext, authentication, eventPublisher);
    }
}
