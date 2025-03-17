package com.sportsphere.sportsphereapi.event.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
@Builder
public class LocationDTO {
    private final String name;
    private final double latitude;
    private final double longitude;
    private final String city;
    private final String country;
    private final String formattedAddress;
}
