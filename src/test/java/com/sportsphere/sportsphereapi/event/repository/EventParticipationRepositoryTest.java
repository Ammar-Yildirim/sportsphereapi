package com.sportsphere.sportsphereapi.event.repository;

import com.sportsphere.sportsphereapi.event.DTO.EventParticipationCountDTO;
import com.sportsphere.sportsphereapi.event.DTO.MonthlyParticipationDTO;
import com.sportsphere.sportsphereapi.event.entity.Event;
import com.sportsphere.sportsphereapi.event.entity.EventParticipation;
import com.sportsphere.sportsphereapi.event.entity.ID.EventParticipationID;
import com.sportsphere.sportsphereapi.event.entity.Location;
import com.sportsphere.sportsphereapi.user.Role;
import com.sportsphere.sportsphereapi.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EventParticipationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EventParticipationRepository eventParticipationRepository;

    private UUID eventId1;
    private UUID eventId2;
    private UUID userId1;
    private UUID userId2;
    private Event event1;
    private Event event2;
    private User user1;
    private User user2;
    private Location location;

    @BeforeEach
    void setUp() {
        location = new Location();
        location.setName("Test Location");
        location.setLatitude(new BigDecimal("40.7128"));
        location.setLongitude(new BigDecimal("-74.0060"));
        location.setCity("New York");
        location.setCountry("USA");
        location.setFormattedAddress("123 Test St, New York, USA");
        entityManager.persist(location);

        user1 = new User();
        user1.setFirstname("John");
        user1.setLastname("Doe");
        user1.setEmail("john.doe@example.com");
        user1.setPassword("password");
        user1.setEnabled(true);
        user1.setRole(Role.USER);
        entityManager.persist(user1);

        user2 = new User();
        user2.setFirstname("Jane");
        user2.setLastname("Doe");
        user2.setEmail("jane.doe@example.com");
        user2.setPassword("password");
        user2.setEnabled(true);
        user2.setRole(Role.USER);
        entityManager.persist(user2);

        userId1 = user1.getId();
        userId2 = user2.getId();

        event1 = new Event();
        event1.setCreatedBy(user1);
        event1.setTitle("Basketball Game");
        event1.setDescription("Friendly match");
        event1.setStartsAt(LocalDateTime.now().minusMonths(1));
        event1.setSportCategory("Basketball");
        event1.setSportName("Streetball");
        event1.setTeamNumber(2);
        event1.setPlayerNumber(10);
        event1.setLocation(location);
        entityManager.persist(event1);

        event2 = new Event();
        event2.setCreatedBy(user2);
        event2.setTitle("Soccer Match");
        event2.setDescription("Tournament");
        event2.setStartsAt(LocalDateTime.now().minusMonths(2));
        event2.setSportCategory("Soccer");
        event2.setSportName("Football");
        event2.setTeamNumber(2);
        event2.setPlayerNumber(22);
        event2.setLocation(location);
        entityManager.persist(event2);

        eventId1 = event1.getId();
        eventId2 = event2.getId();

        EventParticipation participation1 = new EventParticipation();
        EventParticipationID id1 = new EventParticipationID();
        id1.setEventID(eventId1);
        id1.setUserID(userId1);
        participation1.setEventParticipationID(id1);
        participation1.setTeam(1);
        participation1.setSpot(1);
        entityManager.persist(participation1);

        EventParticipation participation2 = new EventParticipation();
        EventParticipationID id2 = new EventParticipationID();
        id2.setEventID(eventId1);
        id2.setUserID(userId2);
        participation2.setEventParticipationID(id2);
        participation2.setTeam(2);
        participation2.setSpot(1);
        entityManager.persist(participation2);

        EventParticipation participation3 = new EventParticipation();
        EventParticipationID id3 = new EventParticipationID();
        id3.setEventID(eventId2);
        id3.setUserID(userId1);
        participation3.setEventParticipationID(id3);
        participation3.setTeam(1);
        participation3.setSpot(1);
        entityManager.persist(participation3);

        entityManager.flush();
    }

    @Test
    void testFindByEventParticipationIDEventID() {
        List<EventParticipation> participations = eventParticipationRepository.findByEventParticipationIDEventID(eventId1);
        assertEquals(2, participations.size());
        assertEquals(eventId1, participations.get(0).getEventParticipationID().getEventID());
    }

    @Test
    void testFindByEventParticipationIDUserIDAndEventParticipationIDEventID() {
        Optional<EventParticipation> participation = eventParticipationRepository
                .findByEventParticipationIDUserIDAndEventParticipationIDEventID(userId1, eventId1);
        assertTrue(participation.isPresent());
        assertEquals(userId1, participation.get().getEventParticipationID().getUserID());
        assertEquals(eventId1, participation.get().getEventParticipationID().getEventID());
    }

    @Test
    void testGetParticipationCounts() {
        List<UUID> eventIds = Arrays.asList(eventId1, eventId2);
        List<EventParticipationCountDTO> counts = eventParticipationRepository.getParticipationCounts(eventIds);
        assertEquals(2, counts.size());
        for (EventParticipationCountDTO dto : counts) {
            if (dto.getEventId().equals(eventId1)) {
                assertEquals(2, dto.getCount());
            } else if (dto.getEventId().equals(eventId2)) {
                assertEquals(1, dto.getCount());
            }
        }
    }

    @Test
    void testGetMonthlyParticipation() {
        List<MonthlyParticipationDTO> monthlyCounts = eventParticipationRepository.getMonthlyParticipation(userId1);
        assertFalse(monthlyCounts.isEmpty());
        for (MonthlyParticipationDTO dto : monthlyCounts) {
            if (dto.getMonth() == event1.getStartsAt().getMonthValue()) {
                assertEquals(1, dto.getParticipationCount());
            }
            if (dto.getMonth() == event2.getStartsAt().getMonthValue()) {
                assertEquals(1, dto.getParticipationCount());
            }
        }
    }

    @Test
    void testDeleteByEventParticipationIDEventID() {
        eventParticipationRepository.deleteByEventParticipationIDEventID(eventId1);
        List<EventParticipation> participations = eventParticipationRepository.findByEventParticipationIDEventID(eventId1);
        assertTrue(participations.isEmpty());

        participations = eventParticipationRepository.findByEventParticipationIDEventID(eventId2);
        assertEquals(1, participations.size());
    }
}