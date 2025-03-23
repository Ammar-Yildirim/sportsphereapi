package com.sportsphere.sportsphereapi.event.repository;

import com.sportsphere.sportsphereapi.event.DTO.EventParticipationCountDTO;
import com.sportsphere.sportsphereapi.event.entity.EventParticipation;
import com.sportsphere.sportsphereapi.event.entity.ID.EventParticipationID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventParticipationRepository extends JpaRepository<EventParticipation, EventParticipationID> {
    List<EventParticipation> findByEventParticipationIDEventID(UUID eventID);

    @Query("SELECT new com.sportsphere.sportsphereapi.event.DTO.EventParticipationCountDTO(ep.eventParticipationID.eventID, COUNT(ep)) " +
            "FROM EventParticipation ep " +
            "WHERE ep.eventParticipationID.eventID IN :eventIds " +
            "GROUP BY ep.eventParticipationID.eventID")
    List<EventParticipationCountDTO> getParticipationCounts(@Param("eventIds") List<UUID> eventIds);
}