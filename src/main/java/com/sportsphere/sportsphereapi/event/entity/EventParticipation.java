package com.sportsphere.sportsphereapi.event.entity;

import com.sportsphere.sportsphereapi.event.entity.ID.EventParticipationID;
import com.sportsphere.sportsphereapi.user.User;
import jakarta.persistence.*;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", insertable = false, updatable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name="team", nullable = false)
    private Integer team;

    @Column(name = "spot", nullable = false)
    private Integer spot;
}
