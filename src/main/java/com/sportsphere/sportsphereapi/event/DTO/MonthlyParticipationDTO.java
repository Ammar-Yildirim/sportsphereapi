package com.sportsphere.sportsphereapi.event.DTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MonthlyParticipationDTO {
    private final Integer month;
    private final Long participationCount;
}
