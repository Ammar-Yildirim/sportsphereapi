package com.sportsphere.sportsphereapi.event.services;

import com.sportsphere.sportsphereapi.event.DTO.LocationDTO;
import com.sportsphere.sportsphereapi.event.DTO.Sport;
import com.sportsphere.sportsphereapi.event.DTO.request.EventRequest;
import com.sportsphere.sportsphereapi.event.entity.Event;
import com.sportsphere.sportsphereapi.event.entity.Location;
import com.sportsphere.sportsphereapi.event.mapper.EventMapper;
import com.sportsphere.sportsphereapi.event.repository.EventParticipationRepository;
import com.sportsphere.sportsphereapi.event.repository.EventRepository;
import com.sportsphere.sportsphereapi.exception.CustomException;
import com.sportsphere.sportsphereapi.user.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private LocationService locationService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private EventService eventService;

    private User testUser;
    private Location testLocation;
    private Event testEvent;
    private EventRequest eventRequest;
    private UUID eventId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);

        userId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(userId);
        lenient().when(authentication.getPrincipal()).thenReturn(testUser);

        eventId = UUID.randomUUID();

        testLocation = Location.builder()
                .id(1)
                .name("Test Location")
                .latitude(new java.math.BigDecimal("51.5074"))
                .longitude(new java.math.BigDecimal("-0.1278"))
                .city("London")
                .country("UK")
                .formattedAddress("123 Test Street, London, UK")
                .build();

        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(5);

        testEvent = Event.builder()
                .id(eventId)
                .title("Test Event")
                .description("Test Description")
                .createdBy(testUser)
                .location(testLocation)
                .startsAt(futureDateTime)
                .sportCategory("Team Sport")
                .sportName("Basketball")
                .teamNumber(2)
                .playerNumber(10)
                .build();

        LocationDTO locationDTO = LocationDTO.builder()
                .name("Test Location")
                .latitude(51.5074)
                .longitude(-0.1278)
                .city("London")
                .country("UK")
                .formattedAddress("123 Test Street, London, UK")
                .build();

        eventRequest = EventRequest.builder()
                .title("Test Event")
                .description("Test Description")
                .startsAt(futureDateTime)
                .sport(Sport.builder()
                        .name("Basketball")
                        .category("Team Sport")
                        .build())
                .teamNumber(2)
                .playerNumber(10)
                .locationDTO(locationDTO)
                .build();
    }

    @Test
    void testCreateEvent_WhenValidInput_ReturnsEventId() {
        when(locationService.createLocation(any(LocationDTO.class))).thenReturn(testLocation);
        when(eventMapper.toEntity(any(EventRequest.class), any(User.class), any(Location.class))).thenReturn(testEvent);
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        UUID result = eventService.createEvent(eventRequest);

        assertThat(result).isEqualTo(eventId);
        verify(locationService).createLocation(eventRequest.getLocationDTO());
        verify(eventMapper).toEntity(eventRequest, testUser, testLocation);
        verify(eventRepository).save(testEvent);
    }

    @Test
    void testGetEventById_WhenEventExists_ReturnsEvent() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));

        Event result = eventService.getById(eventId);

        assertThat(result).isEqualTo(testEvent);
        verify(eventRepository).findById(eventId);
    }

    @Test
    void testGetEventById_WhenEventNotFound_ThrowsException() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventService.getById(eventId));
        verify(eventRepository).findById(eventId);
    }

    @Test
    void testDeleteEvent_WhenUserIsCreator_Succeeds() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));

        UUID result = eventService.deleteEvent(eventId);

        assertThat(result).isEqualTo(eventId);
        verify(eventRepository).findById(eventId);
        verify(eventParticipationRepository).deleteByEventParticipationIDEventID(eventId);
        verify(eventRepository).delete(testEvent);
    }

    @Test
    void testDeleteEvent_WhenUserNotCreator_ThrowsException() {
        User differentUser = new User();
        differentUser.setId(UUID.randomUUID());

        Event eventWithDifferentCreator = Event.builder()
                .id(eventId)
                .createdBy(differentUser)
                .title("Test Event")
                .description("Test Description")
                .startsAt(LocalDateTime.now().plusDays(5))
                .build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(eventWithDifferentCreator));

        assertThrows(CustomException.class, () -> eventService.deleteEvent(eventId));
        verify(eventRepository).findById(eventId);
        verify(eventParticipationRepository, never()).deleteByEventParticipationIDEventID(any(UUID.class));
        verify(eventRepository, never()).delete(any(Event.class));
    }

    @Test
    void testDeleteEvent_WhenEventIsPast_ThrowsException() {
        Event pastEvent = Event.builder()
                .id(eventId)
                .createdBy(testUser)
                .title("Past Event")
                .description("Past Description")
                .startsAt(LocalDateTime.now().minusDays(1))
                .build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(pastEvent));

        assertThrows(CustomException.class, () -> eventService.deleteEvent(eventId));
        verify(eventRepository).findById(eventId);
        verify(eventParticipationRepository, never()).deleteByEventParticipationIDEventID(any(UUID.class));
        verify(eventRepository, never()).delete(any(Event.class));
    }

    @Test
    void testGetUpcomingEvents_WhenLocationFilterApplied_ReturnsFilteredEvents() {
        Double latitude = 51.5074;
        Double longitude = -0.1278;
        List<Event> expectedEvents = Arrays.asList(testEvent);

        when(eventRepository.findUpcomingEventsWithinDistance(latitude, longitude, 30000))
                .thenReturn(expectedEvents);

        List<Event> result = eventService.getUpcomingEvents(latitude, longitude);

        assertThat(result).isEqualTo(expectedEvents);
        verify(eventRepository).findUpcomingEventsWithinDistance(latitude, longitude, 30000);
        verify(eventRepository, never()).findUpcomingEvents();
    }

    @Test
    void testGetUpcomingEvents_WhenNoLocationFilter_ReturnsAllEvents() {
        List<Event> expectedEvents = Arrays.asList(testEvent);

        when(eventRepository.findUpcomingEvents()).thenReturn(expectedEvents);

        List<Event> result = eventService.getUpcomingEvents(null, null);

        assertThat(result).isEqualTo(expectedEvents);
        verify(eventRepository, never()).findUpcomingEventsWithinDistance(anyDouble(), anyDouble(), anyDouble());
        verify(eventRepository).findUpcomingEvents();
    }

    @Test
    void testGetUpcomingEventsByCreator_WhenCreatorExists_ReturnsEvents() {
        List<Event> expectedEvents = Arrays.asList(testEvent);

        when(eventRepository.findUpcomingEventsByCreator(userId)).thenReturn(expectedEvents);

        List<Event> result = eventService.getUpcomingEventsByCreator();

        assertThat(result).isEqualTo(expectedEvents);
        verify(eventRepository).findUpcomingEventsByCreator(userId);
    }

    @Test
    void testGetPastEventsByCreator_WhenCreatorExists_ReturnsEvents() {
        List<Event> expectedEvents = Arrays.asList(testEvent);

        when(eventRepository.findPastEventsByCreator(userId)).thenReturn(expectedEvents);

        List<Event> result = eventService.getPastEventsByCreator();

        assertThat(result).isEqualTo(expectedEvents);
        verify(eventRepository).findPastEventsByCreator(userId);
    }

    @Test
    void testGetUpcomingEventsByParticipant_WhenParticipantExists_ReturnsEvents() {
        List<Event> expectedEvents = Arrays.asList(testEvent);

        when(eventRepository.findUpcomingEventsByParticipant(userId)).thenReturn(expectedEvents);

        List<Event> result = eventService.getUpcomingEventsByParticipant();

        assertThat(result).isEqualTo(expectedEvents);
        verify(eventRepository).findUpcomingEventsByParticipant(userId);
    }

    @Test
    void testGetPastEventsByParticipant_WhenParticipantExists_ReturnsEvents() {
        List<Event> expectedEvents = Arrays.asList(testEvent);

        when(eventRepository.findPastEventsByParticipant(userId)).thenReturn(expectedEvents);

        List<Event> result = eventService.getPastEventsByParticipant();

        assertThat(result).isEqualTo(expectedEvents);
        verify(eventRepository).findPastEventsByParticipant(userId);
    }

    @Test
    void testGetUpcomingEventsByCreator_WhenExceptionOccurs_HandlesException() {
        when(eventRepository.findUpcomingEventsByCreator(userId)).thenThrow(new RuntimeException("Test exception"));

        assertThrows(CustomException.class, () -> eventService.getUpcomingEventsByCreator());
    }

    @Test
    void testGetPastEventsByCreator_WhenExceptionOccurs_HandlesException() {
        when(eventRepository.findPastEventsByCreator(userId)).thenThrow(new RuntimeException("Test exception"));

        assertThrows(CustomException.class, () -> eventService.getPastEventsByCreator());
    }

    @Test
    void testGetUpcomingEventsByParticipant_WhenExceptionOccurs_HandlesException() {
        when(eventRepository.findUpcomingEventsByParticipant(userId)).thenThrow(new RuntimeException("Test exception"));

        assertThrows(CustomException.class, () -> eventService.getUpcomingEventsByParticipant());
    }

    @Test
    void testGetPastEventsByParticipant_WhenExceptionOccurs_HandlesException() {
        when(eventRepository.findPastEventsByParticipant(userId)).thenThrow(new RuntimeException("Test exception"));

        assertThrows(CustomException.class, () -> eventService.getPastEventsByParticipant());
    }
}