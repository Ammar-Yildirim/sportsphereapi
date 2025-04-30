package com.sportsphere.sportsphereapi.event.mapper;

import com.sportsphere.sportsphereapi.event.DTO.request.EventParticipationRequest;
import com.sportsphere.sportsphereapi.event.DTO.response.EventParticipationResponse;
import com.sportsphere.sportsphereapi.event.entity.EventParticipation;
import com.sportsphere.sportsphereapi.event.entity.ID.EventParticipationID;
import com.sportsphere.sportsphereapi.user.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EventParticipationMapper {
    public EventParticipation toEntity(EventParticipationRequest request, User user, UUID eventId) {
        return EventParticipation.builder()
                .eventParticipationID(EventParticipationID.builder()
                        .eventID(eventId)
                        .userID(user.getId())
                        .build())
                .user(user)
                .spot(request.getSpot())
                .team(request.getTeam())
                .build();
    }

    public EventParticipationResponse toEventParticipationResponse(EventParticipation entity) {
        return EventParticipationResponse.builder()
                .eventID(entity.getEventParticipationID().getEventID())
                .userID(entity.getEventParticipationID().getUserID())
                .userName(entity.getUser().getFirstname())
                .team(entity.getTeam())
                .spot(entity.getSpot())
                .build();
    }
}
