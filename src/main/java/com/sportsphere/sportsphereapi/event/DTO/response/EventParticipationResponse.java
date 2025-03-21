package com.sportsphere.sportsphereapi.event.DTO.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
@Builder
public class EventParticipationResponse {
    private final UUID userID;
    private final UUID eventID;
    private final String userName;
    private final Integer team;
    private final Integer spot;
}
