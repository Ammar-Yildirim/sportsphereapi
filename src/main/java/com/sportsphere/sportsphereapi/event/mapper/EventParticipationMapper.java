package com.sportsphere.sportsphereapi.event.mapper;

import com.sportsphere.sportsphereapi.event.DTO.EventParticipationDTO;
import com.sportsphere.sportsphereapi.event.entity.EventParticipation;
import com.sportsphere.sportsphereapi.event.entity.ID.EventParticipationID;
import org.springframework.stereotype.Component;

@Component
public class EventParticipationMapper {
    public EventParticipation toEntity(EventParticipationDTO dto) {
        return EventParticipation.builder()
                .eventParticipationID(EventParticipationID.builder()
                        .eventID(dto.getEventID())
                        .userID(dto.getUserID())
                        .build())
                .spot(dto.getSpot())
                .team(dto.getTeam())
                .build();
    }

    public EventParticipationDTO toDTO(EventParticipation entity) {
        return EventParticipationDTO.builder()
                .eventID(entity.getEventParticipationID().getEventID())
                .userID(entity.getEventParticipationID().getUserID())
                .team(entity.getTeam())
                .spot(entity.getSpot())
                .build();
    }
}
