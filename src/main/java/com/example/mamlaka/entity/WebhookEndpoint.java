package com.example.mamlaka.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class WebhookEndpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url; // The URL to send the webhook notification to

    @Column(nullable = false)
    private String eventType; // For example, PAYMENT_STATUS_CHANGE

    @Column(nullable = false)
    private String secretKey; // Secret key for validating the webhook on the receiver side
}
