package com.sportsphere.sportsphereapi.event.services;

import com.sportsphere.sportsphereapi.event.DTO.EventParticipationCountDTO;
import com.sportsphere.sportsphereapi.event.DTO.MonthlyParticipationDTO;
import com.sportsphere.sportsphereapi.event.DTO.request.EventParticipationRequest;
import com.sportsphere.sportsphereapi.event.entity.Event;
import com.sportsphere.sportsphereapi.event.entity.EventParticipation;
import com.sportsphere.sportsphereapi.event.entity.ID.EventParticipationID;
import com.sportsphere.sportsphereapi.event.mapper.EventParticipationMapper;
import com.sportsphere.sportsphereapi.event.repository.EventParticipationRepository;
import com.sportsphere.sportsphereapi.exception.CustomException;
import com.sportsphere.sportsphereapi.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventParticipationServiceTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private EventService eventService;

    @Mock
    private EventParticipationMapper eventParticipationMapper;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private EventParticipationService participationService;

    private User testUser;
    private Event testEvent;
    private EventParticipation testParticipation;
    private EventParticipationRequest testRequest;
    private UUID eventId;
    private UUID userId;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        eventId = UUID.randomUUID();

        testUser = User.builder()
                .id(userId)
                .email("test@example.com")
                .firstname("John")
                .lastname("Doe")
                .build();

        testEvent = Event.builder()
                .id(eventId)
                .title("Test Event")
                .description("Test Description")
                .startsAt(LocalDateTime.now().plusDays(1))
                .sportCategory("Test Category")
                .sportName("Test Sport")
                .build();

        EventParticipationID participationID = EventParticipationID.builder()
                .eventID(eventId)
                .userID(userId)
                .build();

        testParticipation = EventParticipation.builder()
                .eventParticipationID(participationID)
                .build();

        testRequest = EventParticipationRequest.builder()
                .team(1)
                .spot(2)
                .build();

        authentication = new UsernamePasswordAuthenticationToken(testUser, null);
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    void testAddParticipation_WhenValidInput_Succeeds() {
        when(eventService.getById(eventId)).thenReturn(testEvent);
        when(eventParticipationMapper.toEntity(testRequest, testUser, eventId)).thenReturn(testParticipation);
        when(eventParticipationRepository.save(testParticipation)).thenReturn(testParticipation);

        EventParticipation result = participationService.addParticipation(eventId,testRequest);

        assertNotNull(result);
        assertEquals(testParticipation, result);

        verify(eventService, times(1)).getById(eventId);
        verify(eventParticipationMapper, times(1)).toEntity(testRequest, testUser, eventId);
        verify(eventParticipationRepository, times(1)).save(testParticipation);
    }

    @Test
    void testAddParticipation_WhenEventIsPast_ThrowsException() {
        Event pastEvent = Event.builder()
                .id(eventId)
                .title("Past Event")
                .description("Past Description")
                .startsAt(LocalDateTime.now().minusDays(1))
                .build();

        when(eventService.getById(eventId)).thenReturn(pastEvent);

        CustomException exception = assertThrows(
                CustomException.class,
                () -> participationService.addParticipation(eventId, testRequest)
        );

        assertEquals("Bad request", exception.getError());
        assertEquals("You can't participate in a past event.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

        verify(eventParticipationRepository, never()).save(any(EventParticipation.class));
    }

    @Test
    void testAddParticipation_WhenSpotTaken_ThrowsException() {
        when(eventService.getById(eventId)).thenReturn(testEvent);
        when(eventParticipationMapper.toEntity(testRequest, testUser, eventId)).thenReturn(testParticipation);
        when(eventParticipationRepository.save(testParticipation)).thenThrow(DataIntegrityViolationException.class);

        CustomException exception = assertThrows(
                CustomException.class,
                () -> participationService.addParticipation(eventId, testRequest)
        );

        assertEquals("Data Integrity Error", exception.getError());
        assertEquals("This spot was occupied, please try another.", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());

        verify(eventService, times(1)).getById(eventId);
        verify(eventParticipationMapper, times(1)).toEntity(testRequest, testUser, eventId);
        verify(eventParticipationRepository, times(1)).save(testParticipation);
    }

    @Test
    void testRemoveParticipation_WhenValidInput_Succeeds() {
        when(eventService.getById(eventId)).thenReturn(testEvent);

        UUID result = participationService.removeParticipation(eventId);

        assertEquals(userId, result);

        verify(eventService, times(1)).getById(eventId);
        verify(eventParticipationRepository, times(1)).deleteById(any(EventParticipationID.class));
    }

    @Test
    void testRemoveParticipation_WhenEventIsPast_ThrowsException() {
        Event pastEvent = Event.builder()
                .id(eventId)
                .title("Past Event")
                .description("Past Description")
                .startsAt(LocalDateTime.now().minusDays(1))
                .build();

        when(eventService.getById(eventId)).thenReturn(pastEvent);

        CustomException exception = assertThrows(
                CustomException.class,
                () -> participationService.removeParticipation(eventId)
        );

        assertEquals("Bad request", exception.getError());
        assertEquals("You can't remove participation in a past event.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

        verify(eventParticipationRepository, never()).deleteById(any(EventParticipationID.class));
    }

    @Test
    void testRemoveParticipation_WhenEventStartingSoon_ThrowsException() {
        Event soonEvent = Event.builder()
                .id(eventId)
                .title("Soon Event")
                .description("Soon Description")
                .startsAt(LocalDateTime.now().plusMinutes(15))
                .build();

        when(eventService.getById(eventId)).thenReturn(soonEvent);

        CustomException exception = assertThrows(
                CustomException.class,
                () -> participationService.removeParticipation(eventId)
        );

        assertEquals("Bad request", exception.getError());
        assertEquals("You can't leave an event with 30 minutes or less left until it starts.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

        verify(eventParticipationRepository, never()).deleteById(any(EventParticipationID.class));
    }

    @Test
    void testRemoveParticipation_WhenDatabaseError_ThrowsException() {
        when(eventService.getById(eventId)).thenReturn(testEvent);
        doThrow(DataIntegrityViolationException.class).when(eventParticipationRepository).deleteById(any(EventParticipationID.class));

        CustomException exception = assertThrows(
                CustomException.class,
                () -> participationService.removeParticipation(eventId)
        );

        assertEquals("Database Error", exception.getError());
        assertEquals("An error occurred while removing participation.", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());

        verify(eventService, times(1)).getById(eventId);
        verify(eventParticipationRepository, times(1)).deleteById(any(EventParticipationID.class));
    }

    @Test
    void testGetEventParticipation_WhenEventExists_ReturnsParticipation() {
        List<EventParticipation> participations = Arrays.asList(testParticipation);
        when(eventParticipationRepository.findByEventParticipationIDEventID(eventId)).thenReturn(participations);

        List<EventParticipation> result = participationService.getEventParticipation(eventId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testParticipation, result.get(0));

        verify(eventParticipationRepository, times(1)).findByEventParticipationIDEventID(eventId);
    }

    @Test
    void testGetParticipationCounts_WhenDataExists_ReturnsCounts() {
        List<UUID> eventIds = Arrays.asList(eventId);
        List<EventParticipationCountDTO> countDTOs = Arrays.asList( new EventParticipationCountDTO(eventId, 5L));

        when(eventParticipationRepository.getParticipationCounts(eventIds)).thenReturn(countDTOs);

        Map<UUID, Long> result = participationService.getParticipationCounts(eventIds);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(eventId));
        assertEquals(5L, result.get(eventId));

        verify(eventParticipationRepository, times(1)).getParticipationCounts(eventIds);
    }

    @Test
    void testGetMonthlyParticipation_WhenDataExists_ReturnsParticipation() {
        List<MonthlyParticipationDTO> monthlyData = Arrays.asList(
                new MonthlyParticipationDTO(4,3L)
        );

        when(eventParticipationRepository.getMonthlyParticipation(userId)).thenReturn(monthlyData);

        List<MonthlyParticipationDTO> result = participationService.getMonthlyParticipation();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(4, result.get(0).getMonth());
        assertEquals(3L, result.get(0).getParticipationCount());

        verify(eventParticipationRepository, times(1)).getMonthlyParticipation(userId);
    }
}