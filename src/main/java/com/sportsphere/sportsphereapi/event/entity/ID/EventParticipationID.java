package com.sportsphere.sportsphereapi.event.entity.ID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventParticipationID implements Serializable {
    @Column(name = "event_id", nullable = false)
    private UUID eventID;
    @Column(name = "user_id", nullable = false)
    private UUID userID;
}
