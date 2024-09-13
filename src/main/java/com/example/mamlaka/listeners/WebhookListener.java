package com.example.mamlaka.listeners;
import com.example.mamlaka.dto.PaymentsResponseDto;
import com.example.mamlaka.entity.WebhookEndpoint;
import com.example.mamlaka.entity.WebhookEvent;
import com.example.mamlaka.events.PaymentStatusChangeEvent;
import com.example.mamlaka.repository.WebhookEndpointRepository;
import com.example.mamlaka.repository.WebhookEventRepository;
import com.example.mamlaka.service.impl.PaymentServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.List;

import com.example.mamlaka.constants.PaymentsConstants;

@Component
@AllArgsConstructor
public class WebhookListener {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final WebhookEndpointRepository webhookEndpointRepository;
    private final WebhookEventRepository webhookEventRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

//    public WebhookListener(WebhookEndpointRepository webhookEndpointRepository) {
//        this.webhookEndpointRepository = webhookEndpointRepository;
//    }

    @TransactionalEventListener
    public void onPaymentStatusChange(PaymentStatusChangeEvent event) {
        // Fetch all webhook endpoints for PAYMENT_STATUS_CHANGE
        List<WebhookEndpoint> webhookEndpoints = webhookEndpointRepository.findByEventType("PAYMENT_STATUS_CHANGE");

        // Send webhook to all registered endpoints
        webhookEndpoints.forEach(endpoint -> {
            sendWebhookNotification(endpoint, event.getPaymentResponse());
        });
    }

    private void sendWebhookNotification(WebhookEndpoint endpoint, PaymentsResponseDto responseDto) {
        // Convert the PaymentsResponseDto to JSON (payload)
        String payload;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            payload = objectMapper.writeValueAsString(responseDto);
        } catch (Exception e) {
            logger.error("Error converting PaymentsResponseDto to JSON", e);
            return;
        }

        // Generate HMAC signature using the secret key and the payload
        String signature = generateHmacSignature(payload, endpoint.getSecretKey());

        // Create a WebhookEvent to track this attempt
        WebhookEvent event = new WebhookEvent();
        event.setWebhookEndpointUrl(endpoint.getUrl());
        event.setPayload(payload);
        event.setCreatedAt(new Date());
        event.setAttempts(0);
        webhookEventRepository.save(event);  // Save the event before sending the request

        // Set up HTTP headers, including the signature
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Hub-Signature", signature);  // Assuming "X-Hub-Signature" is the header used for the signature

        // Create the HTTP entity with headers and payload
        HttpEntity<String> request = new HttpEntity<>(payload, headers);

        try {
            // Send the webhook to the specified URL
            ResponseEntity<String> response = restTemplate.postForEntity(endpoint.getUrl(), request, String.class);

            // Log success and update event status
            if (response.getStatusCode().is2xxSuccessful()) {
                event.setResponseStatus("SUCCESS");
            } else {
                event.setResponseStatus("FAILED");
            }
        } catch (Exception e) {
            // Log failure and increment attempts
            event.setResponseStatus("FAILED");
            event.setAttempts(event.getAttempts() + 1);
            logger.error("Error sending webhook to {}", endpoint.getUrl(), e);
        } finally {
            // Save the event details regardless of success or failure
            webhookEventRepository.save(event);
        }
    }

    private String generateHmacSignature(String payload, String secretKey) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            sha256Hmac.init(secretKeySpec);
            byte[] signedBytes = sha256Hmac.doFinal(payload.getBytes());
            return "sha256=" + Hex.encodeHexString(signedBytes);  // Example signature format
        } catch (Exception e) {
            throw new RuntimeException("Error generating HMAC signature", e);
        }
    }

}
