package com.example.mamlaka.service;

import com.example.mamlaka.constants.PaymentsConstants;
import com.example.mamlaka.entity.WebhookEvent;
import com.example.mamlaka.repository.WebhookEventRepository;
import com.example.mamlaka.service.impl.PaymentServiceImpl;
import lombok.AllArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class WebhookRetryService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookRetryService.class);

    private final WebhookEventRepository webhookEventRepository;
    private final RestTemplate restTemplate;

//    public WebhookRetryService(WebhookEventRepository webhookEventRepository, RestTemplate restTemplate) {
//        this.webhookEventRepository = webhookEventRepository;
//        this.restTemplate = restTemplate;
//    }

    @Scheduled(fixedRate = 60000)  // Runs every 60 seconds
    public void retryFailedWebhooks() {
        Date now = new Date();
        List<WebhookEvent> failedEvents = webhookEventRepository.findFailedEventsWithPendingRetries(now);

        for (WebhookEvent event : failedEvents) {
            if (event.getNextRetryAt().before(now)) {
                sendWebhookNotificationAgain(event);  // Retry the webhook
            }
        }
    }

    private void sendWebhookNotificationAgain(WebhookEvent event) {
        try {
            // Regenerate payload or use existing one
            String payload = event.getPayload();

            // Send webhook
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Signature", generateHmacSignature(payload, event.getWebhookEndpointUrl())); // Example signature

            HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity(event.getWebhookEndpointUrl(), requestEntity, String.class);

            // If successful, update status and stop retries
            event.setResponseStatus("SUCCESS");
            logger.info("Webhook successfully sent to {}", event.getWebhookEndpointUrl());

        } catch (Exception e) {
            // Increment attempt counter, log error, and set the next retry time
            event.setAttempts(event.getAttempts() + 1);
            event.setResponseStatus("FAILED");
            event.setLastAttempt(new Date());

            logger.error("Failed to send webhook to {}. Attempt {}", event.getWebhookEndpointUrl(), event.getAttempts(), e);

            if (event.getAttempts() < PaymentsConstants.MAX_RETRIES) {
                event.setNextRetryAt(new Date(System.currentTimeMillis() + getRetryDelay(event.getAttempts())));
            } else {
                logger.error("Max retries reached for webhook to {}. Giving up.", event.getWebhookEndpointUrl());
            }
        } finally {
            // Save the updated event
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

    private long getRetryDelay(int attempts) {
        // Exponential backoff delay logic (e.g., 5s, 10s, 20s, etc.)
        return PaymentsConstants.RETRY_DELAY_MS * (1L << (attempts - 1)); // This gives an exponential backoff
    }
}
