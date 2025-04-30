package com.sportsphere.sportsphereapi.event.services;

import com.sportsphere.sportsphereapi.event.DTO.LocationDTO;
import com.sportsphere.sportsphereapi.event.entity.Location;
import com.sportsphere.sportsphereapi.event.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationService locationService;

    private LocationDTO locationDTO;
    private Location existingLocation;
    private BigDecimal latitude;
    private BigDecimal longitude;

    @BeforeEach
    void setUp() {
        double rawLatitude = 51.5074;
        double rawLongitude = -0.1278;

        latitude = BigDecimal.valueOf(rawLatitude).setScale(7, RoundingMode.FLOOR);
        longitude = BigDecimal.valueOf(rawLongitude).setScale(7, RoundingMode.FLOOR);

        locationDTO = LocationDTO.builder()
                .name("Test Location")
                .latitude(rawLatitude)
                .longitude(rawLongitude)
                .city("London")
                .country("UK")
                .formattedAddress("123 Test Street, London, UK")
                .build();

        existingLocation = Location.builder()
                .id(1)
                .name("Existing Location")
                .latitude(latitude)
                .longitude(longitude)
                .city("London")
                .country("UK")
                .formattedAddress("123 Existing Street, London, UK")
                .build();
    }

    @Test
    void testGetLocation_WhenCoordinatesMatch_ReturnsExistingLocation() {
        when(locationRepository.findByLatitudeAndLongitude(latitude, longitude))
                .thenReturn(Optional.of(existingLocation));

        Location result = locationService.createLocation(locationDTO);

        assertThat(result).isEqualTo(existingLocation);
        verify(locationRepository).findByLatitudeAndLongitude(latitude, longitude);
        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    void testCreateLocation_WhenNoMatchFound_CreatesNewLocation() {
        when(locationRepository.findByLatitudeAndLongitude(latitude, longitude))
                .thenReturn(Optional.empty());

        Location newLocation = Location.builder()
                .name(locationDTO.getName())
                .latitude(latitude)
                .longitude(longitude)
                .city(locationDTO.getCity())
                .country(locationDTO.getCountry())
                .formattedAddress(locationDTO.getFormattedAddress())
                .build();

        when(locationRepository.save(any(Location.class))).thenReturn(newLocation);

        Location result = locationService.createLocation(locationDTO);

        assertThat(result).isEqualToComparingFieldByField(newLocation);
        verify(locationRepository).findByLatitudeAndLongitude(latitude, longitude);
        verify(locationRepository).save(any(Location.class));
    }

    @Test
    void testScaleCoordinates_WhenCalled_ScalesCorrectly() {
        double rawLatitude = 51.5074123456789;
        double rawLongitude = -0.1278123456789;

        BigDecimal expectedLatitude = BigDecimal.valueOf(rawLatitude).setScale(7, RoundingMode.FLOOR);
        BigDecimal expectedLongitude = BigDecimal.valueOf(rawLongitude).setScale(7, RoundingMode.FLOOR);

        LocationDTO preciseLocationDTO = LocationDTO.builder()
                .name("Precise Location")
                .latitude(rawLatitude)
                .longitude(rawLongitude)
                .city("London")
                .country("UK")
                .formattedAddress("123 Precise Street, London, UK")
                .build();

        when(locationRepository.findByLatitudeAndLongitude(expectedLatitude, expectedLongitude))
                .thenReturn(Optional.empty());

        when(locationRepository.save(any(Location.class))).thenAnswer(invocation -> {
            Location savedLocation = invocation.getArgument(0);
            return savedLocation;
        });

        Location result = locationService.createLocation(preciseLocationDTO);

        assertThat(result.getLatitude()).isEqualTo(expectedLatitude);
        assertThat(result.getLongitude()).isEqualTo(expectedLongitude);
        verify(locationRepository).findByLatitudeAndLongitude(expectedLatitude, expectedLongitude);
    }
}