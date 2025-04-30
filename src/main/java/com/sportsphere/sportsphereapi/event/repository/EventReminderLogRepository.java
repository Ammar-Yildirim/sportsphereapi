package com.sportsphere.sportsphereapi.event.repository;

import com.sportsphere.sportsphereapi.event.EventReminderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface EventReminderLogRepository extends JpaRepository<EventReminderLog, Long> {

    boolean existsByEventIdAndUserId(UUID eventId, UUID userId);

    void deleteBySentAtBefore(LocalDateTime cutoff);
}