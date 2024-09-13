//package com.example.mamlaka.scheduler;
//
//import com.example.mamlaka.entity.WebhookEvent;
//import com.example.mamlaka.repository.WebhookEndpointRepository;
//import com.example.mamlaka.repository.WebhookEventRepository;
//import lombok.AllArgsConstructor;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.List;
//
//@Component
//@AllArgsConstructor
//public class WebhookRetryScheduler {
//
//    private final WebhookEventRepository webhookEventRepository;
//    private final WebhookEndpointRepository webhookEndpointRepository;
//    private final RestTemplate restTemplate;
//
//    @Scheduled(fixedDelay = 60000) // Retry every 1 minute
//    public void retryFailedWebhooks() {
//        List<WebhookEvent> failedEvents = webhookEventRepository.findAllByResponseStatus("FAILED");
//
//        for (WebhookEvent event : failedEvents) {
//            try {
//                restTemplate.postForEntity(event.getWebhookEndpointUrl(), event.getPayload(), String.class);
//                event.setResponseStatus("SUCCESS");
//            } catch (Exception e) {
//                event.setAttempts(event.getAttempts() + 1);
//            } finally {
//                webhookEventRepository.save(event);
//            }
//        }
//    }
//}
