package com.example.mamlaka.service;

import com.example.mamlaka.dto.PaymentsResponseDto;
import com.example.mamlaka.entity.IdempotencyRecord;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Optional;

public interface IIdempotencyService {

    /**
     * Save Idempotency Record
     * @param idempotencyKey - Idempotency Key
     * @param paymentsResponseDto - PaymentsResponseDto Object
     * @throws JsonProcessingException - JsonProcessingException
     */
    void saveIdempotencyRecord(String idempotencyKey, PaymentsResponseDto paymentsResponseDto) throws JsonProcessingException;

    /**
     * Get Idempotency Record
     * @param idempotencyKey - Idempotency Key
     * @return Optional<IdempotencyRecord>
     */
    Optional<IdempotencyRecord> getIdempotencyRecord(String idempotencyKey);

}
