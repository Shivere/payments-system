package com.example.mamlaka.service.impl;

import com.example.mamlaka.dto.PaymentsResponseDto;
import com.example.mamlaka.entity.IdempotencyRecord;
import com.example.mamlaka.repository.IdempotencyRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class IIdempotencyServiceImplTest {

    @Mock
    private IdempotencyRepository idempotencyRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private IIdempotencyServiceImpl idempotencyServiceImpl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveIdempotencyRecord_Success() throws JsonProcessingException {
        PaymentsResponseDto paymentsResponseDto = new PaymentsResponseDto();
        paymentsResponseDto.setTransactionId("txn-123");
        String idempotencyKey = "unique-key";
        String serializedResponse = "serialized-response";

        when(objectMapper.writeValueAsString(any(PaymentsResponseDto.class))).thenReturn(serializedResponse);

        assertDoesNotThrow(() -> idempotencyServiceImpl.saveIdempotencyRecord(idempotencyKey, paymentsResponseDto));

        ArgumentCaptor<IdempotencyRecord> captor = ArgumentCaptor.forClass(IdempotencyRecord.class);
        verify(idempotencyRepository, times(1)).save(captor.capture());
        IdempotencyRecord savedRecord = captor.getValue();

        assertNotNull(savedRecord);
        assertEquals(idempotencyKey, savedRecord.getIdempotencyKey());
        assertEquals("txn-123", savedRecord.getTransactionId());
        assertEquals(serializedResponse, savedRecord.getResponse());
    }

    @Test
    public void testSaveIdempotencyRecord_JsonProcessingException() throws JsonProcessingException {
        PaymentsResponseDto paymentsResponseDto = new PaymentsResponseDto();
        String idempotencyKey = "unique-key";

        when(objectMapper.writeValueAsString(any(PaymentsResponseDto.class))).thenThrow(JsonProcessingException.class);

        assertThrows(JsonProcessingException.class, () -> idempotencyServiceImpl.saveIdempotencyRecord(idempotencyKey, paymentsResponseDto));

        verify(idempotencyRepository, times(0)).save(any(IdempotencyRecord.class));
    }

    @Test
    public void testGetIdempotencyRecord_Found() {
        IdempotencyRecord idempotencyRecord = new IdempotencyRecord();
        idempotencyRecord.setIdempotencyKey("unique-key");

        when(idempotencyRepository.findByIdempotencyKey(anyString())).thenReturn(Optional.of(idempotencyRecord));

        Optional<IdempotencyRecord> result = idempotencyServiceImpl.getIdempotencyRecord("unique-key");

        assertTrue(result.isPresent());
        assertEquals(idempotencyRecord, result.get());
        verify(idempotencyRepository, times(1)).findByIdempotencyKey("unique-key");
    }

    @Test
    public void testGetIdempotencyRecord_NotFound() {
        when(idempotencyRepository.findByIdempotencyKey(anyString())).thenReturn(Optional.empty());

        Optional<IdempotencyRecord> result = idempotencyServiceImpl.getIdempotencyRecord("unique-key");

        assertFalse(result.isPresent());
        verify(idempotencyRepository, times(1)).findByIdempotencyKey("unique-key");
    }

    @AfterEach
    public void tearDown() {
        reset(idempotencyRepository, objectMapper);
    }
}
