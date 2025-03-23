package com.sportsphere.sportsphereapi.event.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EventParticipationCountDTO {
    private UUID eventId;
    private Long count;
}
