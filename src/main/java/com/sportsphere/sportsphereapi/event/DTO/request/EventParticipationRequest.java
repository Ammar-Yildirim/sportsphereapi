package com.sportsphere.sportsphereapi.event.DTO.request;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
@Builder
public class EventParticipationRequest {
    private final UUID eventID;
    private final Integer team;
    private final Integer spot;
}
