package com.example.mamlaka.repository;

import com.example.mamlaka.entity.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, Long> {

    List<WebhookEvent> findAllByResponseStatus(String responseStatus);

    @Query("SELECT e FROM WebhookEvent e WHERE e.responseStatus = 'FAILED' AND e.nextRetryAt <= :now")
    List<WebhookEvent> findFailedEventsWithPendingRetries(@Param("now") Date now);
}
