package com.sportsphere.sportsphereapi.event;


import com.sportsphere.sportsphereapi.event.entity.Event;
import com.sportsphere.sportsphereapi.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "event_reminder_log")
@Getter
@Setter
public class EventReminderLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "status", nullable = false, length = 10)
    private String status;

    @Column(name = "error_message")
    private String errorMessage;
}