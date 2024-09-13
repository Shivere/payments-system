package com.example.mamlaka.controller;

import com.example.mamlaka.entity.WebhookEndpoint;
import com.example.mamlaka.repository.WebhookEndpointRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class WebhookRegistrationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WebhookEndpointRepository webhookEndpointRepository;

    @InjectMocks
    private WebhookRegistrationController webhookRegistrationController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(webhookRegistrationController).build();
    }

    @Test
    public void testRegisterWebhook_Success() throws Exception {
        WebhookEndpoint webhookEndpoint = new WebhookEndpoint();
        webhookEndpoint.setUrl("https://example.com/webhook");
        webhookEndpoint.setEventType("PAYMENT_STATUS_CHANGE");
        webhookEndpoint.setSecretKey("secretKey");

        when(webhookEndpointRepository.save(any(WebhookEndpoint.class))).thenReturn(webhookEndpoint);

        mockMvc.perform(post("/api/webhooks/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(webhookEndpoint)))
                .andExpect(status().isOk())
                .andDo(print());
    }

//    @Test
//    public void testRegisterWebhook_NullUrl() throws Exception {
//        WebhookEndpoint webhookEndpoint = new WebhookEndpoint();
//        webhookEndpoint.setEventType("PAYMENT_STATUS_CHANGE");
//        webhookEndpoint.setSecretKey("secretKey");
//
//        mockMvc.perform(post("/api/webhooks/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(webhookEndpoint)))
//                .andExpect(status().isBadRequest())  // Expect a 400 Bad Request due to null URL
//                .andDo(print());
//    }
//
//    @Test
//    public void testRegisterWebhook_InvalidUrl() throws Exception {
//        WebhookEndpoint webhookEndpoint = new WebhookEndpoint();
//        webhookEndpoint.setUrl("invalid-url");  // Invalid URL format
//        webhookEndpoint.setEventType("PAYMENT_STATUS_CHANGE");
//        webhookEndpoint.setSecretKey("secretKey");
//
//        mockMvc.perform(post("/api/webhooks/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(webhookEndpoint)))
//                .andExpect(status().isBadRequest())  // Expect a 400 Bad Request due to invalid URL
//                .andDo(print());
//    }
}
