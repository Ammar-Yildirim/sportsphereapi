package com.sportsphere.sportsphereapi.event.repository;

import com.sportsphere.sportsphereapi.event.entity.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class LocationRepositoryTest {

    @Autowired
    private LocationRepository locationRepository;

    private Location testLocation;

    @BeforeEach
    void setUp() {
        testLocation = new Location();
        testLocation.setName("Central Park");
        testLocation.setLatitude(new BigDecimal("40.712776"));
        testLocation.setLongitude(new BigDecimal("-74.005974"));
        testLocation.setFormattedAddress("New York, NY");

        locationRepository.save(testLocation);
    }

    @Test
    void testFindByLatitudeAndLongitude_ShouldReturnLocation() {
        BigDecimal latitude = new BigDecimal("40.712776");
        BigDecimal longitude = new BigDecimal("-74.005974");

        Optional<Location> foundLocation = locationRepository.findByLatitudeAndLongitude(latitude, longitude);

        assertTrue(foundLocation.isPresent(), "Location should be found");
        assertEquals(latitude, foundLocation.get().getLatitude(), "Latitude should match");
        assertEquals(longitude, foundLocation.get().getLongitude(), "Longitude should match");
        assertEquals("New York, NY", foundLocation.get().getFormattedAddress(), "Formatted address should match");
    }

    @Test
    void testFindByLatitudeAndLongitude_ShouldReturnEmpty_WhenLocationNotFound() {
        BigDecimal latitude = new BigDecimal("50.000000");
        BigDecimal longitude = new BigDecimal("-100.000000");

        Optional<Location> foundLocation = locationRepository.findByLatitudeAndLongitude(latitude, longitude);

        assertFalse(foundLocation.isPresent(), "Location should not be found");
    }
}