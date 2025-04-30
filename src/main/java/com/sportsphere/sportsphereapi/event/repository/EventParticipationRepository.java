package com.sportsphere.sportsphereapi.event.repository;

import com.sportsphere.sportsphereapi.event.DTO.EventParticipationCountDTO;
import com.sportsphere.sportsphereapi.event.DTO.MonthlyParticipationDTO;
import com.sportsphere.sportsphereapi.event.entity.EventParticipation;
import com.sportsphere.sportsphereapi.event.entity.ID.EventParticipationID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventParticipationRepository extends JpaRepository<EventParticipation, EventParticipationID> {
    List<EventParticipation> findByEventParticipationIDEventID(UUID eventID);
    Optional<EventParticipation> findByEventParticipationIDUserIDAndEventParticipationIDEventID(
            UUID userId, UUID eventId);
    boolean existsByEventParticipationID_EventIDAndTeamAndSpot(UUID eventId, Integer team, Integer spot);

    //    @Query(value = "SELECT event_id, count(*) " +
//            "FROM event_participation " +
//            "WHERE event_id IN :eventIds " +
//            "GROUP BY event_id", nativeQuery = true)
@Query("""
    SELECT new com.sportsphere.sportsphereapi.event.DTO.EventParticipationCountDTO(
        ep.eventParticipationID.eventID, COUNT(ep)
    )
    FROM EventParticipation ep
    WHERE ep.eventParticipationID.eventID IN :eventIds
    GROUP BY ep.eventParticipationID.eventID
    """)
    List<EventParticipationCountDTO> getParticipationCounts(@Param("eventIds") List<UUID> eventIds);

    @Query(value = "SELECT EXTRACT(MONTH FROM e.starts_at)::integer AS \"month\", count(*) " +
            "FROM event_participation ep " +
            "JOIN events e " +
            "ON ep.event_id = e.id " +
            "WHERE user_id = :userId " +
            "AND e.starts_at >= DATE_TRUNC('year', current_date) " +
            "AND e.starts_at <= current_timestamp " +
            "GROUP BY EXTRACT(MONTH FROM e.starts_at);", nativeQuery = true)
    List<MonthlyParticipationDTO> getMonthlyParticipation(@Param("userId") UUID userId);

    void deleteByEventParticipationIDEventID(UUID eventId);
}