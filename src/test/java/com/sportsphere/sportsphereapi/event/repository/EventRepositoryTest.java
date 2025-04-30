package com.sportsphere.sportsphereapi.event.repository;

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
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
class EventRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EventRepository eventRepository;

    private User testUser;
    private User otherUser;
    private Location testLocation;
    private Location farLocation;
    private Event upcomingEvent;
    private Event pastEvent;
    private Event upcomingFarEvent;
    private Event upcomingOtherUserEvent;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .firstname("John")
                .lastname("Doe")
                .password("encrypted-pass")
                .email("john@gmail.com")
                .enabled(true)
                .role(Role.USER)
                .build();
        entityManager.persist(testUser);

        otherUser = User.builder()
                .firstname("Jane")
                .lastname("Doe")
                .password("encrypted-pass")
                .email("jane@gmail.com")
                .enabled(true)
                .role(Role.USER)
                .build();
        entityManager.persist(otherUser);

        testLocation = Location.builder()
                .name("Central Park")
                .latitude(new BigDecimal("40.7829"))
                .longitude(new BigDecimal("-73.9654"))
                .city("New York")
                .country("USA")
                .formattedAddress("Central Park, New York, NY")
                .build();
        entityManager.persist(testLocation);

        farLocation = Location.builder()
                .name("Golden Gate Park")
                .latitude(new BigDecimal("37.7694"))
                .longitude(new BigDecimal("-122.4862"))
                .city("San Francisco")
                .country("USA")
                .formattedAddress("Golden Gate Park, San Francisco, CA")
                .build();
        entityManager.persist(farLocation);

        upcomingEvent = Event.builder()
                .createdBy(testUser)
                .title("Soccer Match")
                .description("Friendly soccer game")
                .startsAt(LocalDateTime.now().plusDays(1))
                .sportCategory("Soccer")
                .sportName("Football")
                .teamNumber(2)
                .playerNumber(11)
                .location(testLocation)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        entityManager.persist(upcomingEvent);

        pastEvent = Event.builder()
                .createdBy(testUser)
                .title("Past Basketball Game")
                .description("Basketball tournament")
                .startsAt(LocalDateTime.now().minusDays(1))
                .sportCategory("Basketball")
                .sportName("Basketball")
                .teamNumber(2)
                .playerNumber(5)
                .location(testLocation)
                .createdAt(LocalDateTime.now().minusDays(2))
                .updatedAt(LocalDateTime.now().minusDays(2))
                .build();
        entityManager.persist(pastEvent);

        upcomingFarEvent = Event.builder()
                .createdBy(testUser)
                .title("Far Soccer Match")
                .description("Soccer game in SF")
                .startsAt(LocalDateTime.now().plusDays(2))
                .sportCategory("Soccer")
                .sportName("Football")
                .teamNumber(2)
                .playerNumber(11)
                .location(farLocation)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        entityManager.persist(upcomingFarEvent);

        upcomingOtherUserEvent = Event.builder()
                .createdBy(otherUser)
                .title("Other User's Soccer Match")
                .description("Soccer game")
                .startsAt(LocalDateTime.now().plusDays(3))
                .sportCategory("Soccer")
                .sportName("Football")
                .teamNumber(2)
                .playerNumber(11)
                .location(testLocation)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        entityManager.persist(upcomingOtherUserEvent);

        EventParticipation participation = EventParticipation.builder()
                .eventParticipationID(new EventParticipationID(upcomingEvent.getId(), testUser.getId()))
                .event(upcomingEvent)
                .user(testUser)
                .team(1)
                .spot(1)
                .build();
        entityManager.persist(participation);

        entityManager.flush();
    }

    @Test
    void testFindUpcomingEventsWithinDistance() {
        double refLat = 40.7829;
        double refLon = -73.9654;
        double distanceInMeters = 10000;

        List<Event> events = eventRepository.findUpcomingEventsWithinDistance(refLat, refLon, distanceInMeters);

        assertThat(events).hasSize(2);
        assertThat(events).extracting("title")
                .containsExactly("Soccer Match", "Other User's Soccer Match");
        assertThat(events.get(0).getStartsAt()).isBefore(events.get(1).getStartsAt());
    }

    @Test
    void testFindUpcomingEvents() {
        List<Event> events = eventRepository.findUpcomingEvents();

        assertThat(events).hasSize(3);
        assertThat(events).extracting("title")
                .containsExactly("Soccer Match", "Far Soccer Match", "Other User's Soccer Match");
        assertThat(events.get(0).getStartsAt()).isBefore(events.get(1).getStartsAt());
        assertThat(events.get(1).getStartsAt()).isBefore(events.get(2).getStartsAt());
    }

    @Test
    void testFindUpcomingEventsByCreator() {
        List<Event> events = eventRepository.findUpcomingEventsByCreator(testUser.getId());

        assertThat(events).hasSize(2);
        assertThat(events).extracting("title")
                .containsExactly("Soccer Match", "Far Soccer Match");
        assertThat(events.get(0).getStartsAt()).isBefore(events.get(1).getStartsAt());
    }

    @Test
    void testFindPastEventsByCreator() {
        List<Event> events = eventRepository.findPastEventsByCreator(testUser.getId());

        assertThat(events).hasSize(1);
        assertThat(events).extracting("title")
                .containsExactly("Past Basketball Game");
    }

    @Test
    void testFindUpcomingEventsByParticipant() {
        List<Event> events = eventRepository.findUpcomingEventsByParticipant(testUser.getId());

        assertThat(events).hasSize(1);
        assertThat(events).extracting("title")
                .containsExactly("Soccer Match");
    }

    @Test
    void testFindPastEventsByParticipant() {
        EventParticipation pastParticipation = EventParticipation.builder()
                .eventParticipationID(new EventParticipationID(pastEvent.getId(), testUser.getId()))
                .event(pastEvent)
                .user(testUser)
                .team(1)
                .spot(1)
                .build();
        entityManager.persist(pastParticipation);
        entityManager.flush();

        List<Event> events = eventRepository.findPastEventsByParticipant(testUser.getId());

        assertThat(events).hasSize(1);
        assertThat(events).extracting("title")
                .containsExactly("Past Basketball Game");
    }
}