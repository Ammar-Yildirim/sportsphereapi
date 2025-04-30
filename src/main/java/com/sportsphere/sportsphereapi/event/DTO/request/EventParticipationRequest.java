package com.sportsphere.sportsphereapi.event.DTO.request;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class EventParticipationRequest {
    private final Integer team;
    private final Integer spot;
}
