package com.sportsphere.sportsphereapi.event.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
@Builder
public class Location {
    private final String name;
    private final Double latitude;
    private final Double longitude;
}
