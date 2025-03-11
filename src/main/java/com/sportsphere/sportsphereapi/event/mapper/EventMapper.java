package com.sportsphere.sportsphereapi.event.mapper;

import com.sportsphere.sportsphereapi.event.DTO.Location;
import com.sportsphere.sportsphereapi.event.DTO.Sport;
import com.sportsphere.sportsphereapi.event.Entity.Event;
import com.sportsphere.sportsphereapi.event.DTO.EventDTO;
import com.sportsphere.sportsphereapi.user.User;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {
    public Event toEntity(EventDTO dto, User user){
        return Event.builder()
                .title(dto.getTitle())
                .createdBy(user)
                .description(dto.getDescription())
                .startsAt(dto.getStartsAt())
                .sportCategory(dto.getSport().getCategory())
                .sportName(dto.getSport().getName())
                .locationName(dto.getLocation().getName())
                .longitude(dto.getLocation().getLongitude())
                .latitude(dto.getLocation().getLatitude())
                .playerNumber(dto.getPlayerNumber())
                .teamNumber(dto.getTeamNumber())
                .build();
    }

    public EventDTO toDTO(Event event){
        return EventDTO.builder()
                .title(event.getTitle())
                .description(event.getDescription())
                .playerNumber(event.getPlayerNumber())
                .teamNumber(event.getTeamNumber())
                .startsAt(event.getStartsAt())
                .location(Location.builder()
                        .name(event.getLocationName())
                        .latitude(event.getLatitude())
                        .longitude(event.getLongitude())
                        .build())
                .sport(Sport.builder()
                        .category(event.getSportCategory())
                        .name(event.getSportName())
                        .build())
                .createdBy(event.getCreatedBy().getFirstname())
                .build();
    }
}
