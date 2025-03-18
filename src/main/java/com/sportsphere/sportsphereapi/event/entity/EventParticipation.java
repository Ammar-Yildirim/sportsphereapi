package com.sportsphere.sportsphereapi.event.entity;

import com.sportsphere.sportsphereapi.event.entity.ID.EventParticipationID;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Table(name = "event_participation")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventParticipation {
    @EmbeddedId
    private EventParticipationID eventParticipationID;

    @Column(name="team", nullable = false)
    private Integer team;

    @Column(name = "spot", nullable = false)
    private Integer spot;
}
