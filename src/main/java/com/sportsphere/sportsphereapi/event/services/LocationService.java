package com.sportsphere.sportsphereapi.event.services;

import com.sportsphere.sportsphereapi.event.DTO.LocationDTO;
import com.sportsphere.sportsphereapi.event.entity.Location;
import com.sportsphere.sportsphereapi.event.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;

    public Location createLocation(LocationDTO locationDTO) {
        Location location;
        BigDecimal latitude = BigDecimal.valueOf(locationDTO.getLatitude())
                .setScale(7, RoundingMode.FLOOR);
        BigDecimal longitude = BigDecimal.valueOf(locationDTO.getLongitude())
                .setScale(7, RoundingMode.FLOOR);
        Optional<Location> existingLocation = locationRepository.findByLatitudeAndLongitude(latitude, longitude);

        if (existingLocation.isPresent()) {
            location = existingLocation.get();
        } else {
            location = Location.builder()
                    .name(locationDTO.getName())
                    .longitude(longitude)
                    .latitude(latitude)
                    .city(locationDTO.getCity())
                    .country(locationDTO.getCountry())
                    .formattedAddress(locationDTO.getFormattedAddress())
                    .build();
            locationRepository.save(location);
        }

        return location;
    }
}
