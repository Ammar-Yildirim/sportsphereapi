package com.sportsphere.sportsphereapi.event.repository;

import com.sportsphere.sportsphereapi.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    @Query(value = "SELECT e.* " +
            "FROM events e JOIN locations l " +
            "ON e.location_id = l.id " +
            "WHERE earth_distance(" +
            "  ll_to_earth(:refLat, :refLon), " +
            "  ll_to_earth(l.latitude, l.longitude)" +
            ") <= :distanceInMeters " +
            "AND e.starts_at > NOW()::timestamp " +
            "ORDER BY e.starts_at", nativeQuery = true)
    List<Event> findUpcomingEventsWithinDistance(@Param("refLat") double refLat, @Param("refLon") double refLon, @Param("distanceInMeters") double distanceInMeters);

    @Query(value = "SELECT * " +
            "FROM events " +
            "WHERE starts_at > NOW()::timestamp " +
            "ORDER BY starts_at", nativeQuery = true)
    List<Event> findUpcomingEvents();

    @Query(value = "SELECT e.* " +
            "FROM events e " +
            "WHERE e.created_by = :userId " +
            "AND e.starts_at > NOW()::timestamp " +
            "ORDER BY e.starts_at", nativeQuery = true)
    List<Event> findUpcomingEventsByCreator(@Param("userId") UUID userId);

    @Query(value = "SELECT e.* " +
            "FROM events e " +
            "WHERE e.created_by = :userId " +
            "AND e.starts_at < NOW()::timestamp " +
            "ORDER BY e.starts_at", nativeQuery = true)
    List<Event> findPastEventsByCreator(@Param("userId") UUID userId);

    @Query(value = "SELECT e.* " +
            "FROM events e " +
            "JOIN event_participation ep " +
            "ON e.id = ep.event_id " +
            "WHERE ep.user_id = :userId " +
            "AND e.starts_at > NOW()::timestamp " +
            "ORDER BY e.starts_at", nativeQuery = true)
    List<Event> findUpcomingEventsByParticipant(@Param("userId") UUID userId);

    @Query(value = "SELECT e.* " +
            "FROM events e " +
            "JOIN event_participation ep " +
            "ON e.id = ep.event_id " +
            "WHERE ep.user_id = :userId " +
            "AND e.starts_at < NOW()::timestamp " +
            "ORDER BY e.starts_at", nativeQuery = true)
    List<Event> findPastEventsByParticipant(@Param("userId") UUID userId);
}
