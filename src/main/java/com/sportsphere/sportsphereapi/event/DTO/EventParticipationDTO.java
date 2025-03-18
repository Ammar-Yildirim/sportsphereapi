package com.sportsphere.sportsphereapi.event.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
@Builder
public class EventParticipationDTO {
    private final UUID userID;
    private final UUID eventID;
    private final Integer team;
    private final Integer spot;
}
