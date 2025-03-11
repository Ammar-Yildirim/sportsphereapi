package com.sportsphere.sportsphereapi.event.services;

import com.sportsphere.sportsphereapi.event.DTO.LocationDTO;
import com.sportsphere.sportsphereapi.event.entity.Location;
import com.sportsphere.sportsphereapi.event.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;

    public Location createLocation(LocationDTO locationDTO) {
        Location location;
        String latitude = locationDTO.getLatitude();
        String longitude = locationDTO.getLongitude();
        Optional<Location> existingLocation = locationRepository.findByLatitudeAndLongitude(latitude, longitude);

        if (existingLocation.isPresent()) {
            location = existingLocation.get();
        } else {
            location = Location.builder()
                    .name(locationDTO.getName())
                    .longitude(locationDTO.getLongitude())
                    .latitude(locationDTO.getLatitude())
                    .build();
            locationRepository.save(location);
        }

        return location;
    }
}
