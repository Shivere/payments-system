package com.example.mamlaka.service.impl;

import com.example.mamlaka.dto.PaymentsResponseDto;
import com.example.mamlaka.entity.IdempotencyRecord;
import com.example.mamlaka.repository.IdempotencyRepository;
import com.example.mamlaka.service.IIdempotencyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class IIdempotencyServiceImpl implements IIdempotencyService {

    private final IdempotencyRepository idempotencyRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void saveIdempotencyRecord(String idempotencyKey, PaymentsResponseDto paymentsResponseDto) throws JsonProcessingException {
        IdempotencyRecord idempotencyRecord = new IdempotencyRecord();
        idempotencyRecord.setIdempotencyKey(idempotencyKey);
        idempotencyRecord.setTransactionId(paymentsResponseDto.getTransactionId());
        idempotencyRecord.setResponse(objectMapper.writeValueAsString(paymentsResponseDto));

        idempotencyRepository.save(idempotencyRecord);
    }

    @Override
    public Optional<IdempotencyRecord> getIdempotencyRecord(String idempotencyKey) {
        return idempotencyRepository.findByIdempotencyKey(idempotencyKey);
    }
}
