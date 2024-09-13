package com.example.mamlaka.controller;

import com.example.mamlaka.entity.WebhookEndpoint;
import com.example.mamlaka.repository.WebhookEndpointRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhooks")
@AllArgsConstructor
public class WebhookRegistrationController {

    private final WebhookEndpointRepository webhookEndpointRepository;

    @PostMapping("/register")
    public ResponseEntity<WebhookEndpoint> registerWebhook(@Valid @RequestBody WebhookEndpoint webhookEndpoint) {
        // Save the webhook endpoint to the database
        WebhookEndpoint savedEndpoint = webhookEndpointRepository.save(webhookEndpoint);
        return ResponseEntity.ok(savedEndpoint);
    }
}
