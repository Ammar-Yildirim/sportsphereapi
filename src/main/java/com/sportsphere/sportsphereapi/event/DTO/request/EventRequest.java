package com.sportsphere.sportsphereapi.event.DTO.request;

import com.sportsphere.sportsphereapi.event.DTO.LocationDTO;
import com.sportsphere.sportsphereapi.event.DTO.Sport;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@RequiredArgsConstructor
@Builder
public class EventRequest {

    private final String title;
    private final String description;
    private final LocalDateTime startsAt;
    private final Integer teamNumber;
    private final Integer playerNumber;
    private final LocationDTO locationDTO;
    private final Sport sport;
}
