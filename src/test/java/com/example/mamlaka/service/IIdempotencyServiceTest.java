package com.example.mamlaka.service;

import com.example.mamlaka.dto.PaymentsResponseDto;
import com.example.mamlaka.entity.IdempotencyRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class IIdempotencyServiceTest {

    @Mock
    private IIdempotencyService idempotencyService;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveIdempotencyRecord_Success() throws JsonProcessingException {
        PaymentsResponseDto paymentsResponseDto = new PaymentsResponseDto();
        String idempotencyKey = "unique-key";

        doNothing().when(idempotencyService).saveIdempotencyRecord(eq(idempotencyKey), eq(paymentsResponseDto));

        assertDoesNotThrow(() -> idempotencyService.saveIdempotencyRecord(idempotencyKey, paymentsResponseDto));
        verify(idempotencyService, times(1)).saveIdempotencyRecord(idempotencyKey, paymentsResponseDto);
    }

    @Test
    public void testSaveIdempotencyRecord_JsonProcessingException() throws JsonProcessingException {
        PaymentsResponseDto paymentsResponseDto = new PaymentsResponseDto();
        String idempotencyKey = "unique-key";

        doThrow(JsonProcessingException.class).when(idempotencyService).saveIdempotencyRecord(eq(idempotencyKey), eq(paymentsResponseDto));

        assertThrows(JsonProcessingException.class, () -> idempotencyService.saveIdempotencyRecord(idempotencyKey, paymentsResponseDto));
    }

    @Test
    public void testGetIdempotencyRecord_Found() {
        String idempotencyKey = "unique-key";
        IdempotencyRecord idempotencyRecord = new IdempotencyRecord();

        when(idempotencyService.getIdempotencyRecord(idempotencyKey)).thenReturn(Optional.of(idempotencyRecord));

        Optional<IdempotencyRecord> result = idempotencyService.getIdempotencyRecord(idempotencyKey);

        assertTrue(result.isPresent());
        assertEquals(idempotencyRecord, result.get());
        verify(idempotencyService, times(1)).getIdempotencyRecord(idempotencyKey);
    }

    @Test
    public void testGetIdempotencyRecord_NotFound() {
        String idempotencyKey = "unique-key";

        when(idempotencyService.getIdempotencyRecord(idempotencyKey)).thenReturn(Optional.empty());

        Optional<IdempotencyRecord> result = idempotencyService.getIdempotencyRecord(idempotencyKey);

        assertFalse(result.isPresent());
        verify(idempotencyService, times(1)).getIdempotencyRecord(idempotencyKey);
    }

    @AfterEach
    public void tearDown() {
        reset(idempotencyService);
    }
}
