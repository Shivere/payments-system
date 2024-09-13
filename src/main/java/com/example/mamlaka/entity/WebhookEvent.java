package com.example.mamlaka.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
//@Table(name = "webhook_event")
@Data
public class WebhookEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String webhookEndpointUrl;

    @Lob
    private String payload; // Store the payload sent to the webhook

    private int attempts = 0; // Tracks how many times the webhook has been tried

    private String responseStatus; // Can be "SUCCESS" or "FAILED"

    private Date lastAttempt; // The last time an attempt was made

    private Date nextRetryAt; // The next scheduled retry time

    private Date createdAt; // When the event was first created
}
