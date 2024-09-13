package com.example.mamlaka.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class PaymentTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    private Date timestamp;

    private String paymentMethod;

    private String phoneNumber;

    private String cardNumber;

    private String status;

    private String description;

    // Mock ID from transaction gateway for testing purposes
    private String transactionId;

    // Add the ManyToOne relationship with the User entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // `user_id` is the foreign key
    private User user;
}
