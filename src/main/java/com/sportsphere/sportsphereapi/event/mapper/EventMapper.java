package com.sportsphere.sportsphereapi.event.mapper;

import com.sportsphere.sportsphereapi.event.DTO.LocationDTO;
import com.sportsphere.sportsphereapi.event.DTO.Sport;
import com.sportsphere.sportsphereapi.event.DTO.response.EventResponse;
import com.sportsphere.sportsphereapi.event.entity.Event;
import com.sportsphere.sportsphereapi.event.DTO.request.EventRequest;
import com.sportsphere.sportsphereapi.event.entity.Location;
import com.sportsphere.sportsphereapi.user.User;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {
    public Event toEntity(EventRequest dto, User user, Location location){
        return Event.builder()
                .title(dto.getTitle())
                .createdBy(user)
                .description(dto.getDescription())
                .startsAt(dto.getStartsAt())
                .sportCategory(dto.getSport().getCategory())
                .sportName(dto.getSport().getName())
                .location(location)
                .playerNumber(dto.getPlayerNumber())
                .teamNumber(dto.getTeamNumber())
                .build();
    }

    public EventResponse toEventResponse(Event event){
        return EventResponse.builder()
                .id(event.getId())
                .userId(event.getCreatedBy().getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .playerNumber(event.getPlayerNumber())
                .teamNumber(event.getTeamNumber())
                .startsAt(event.getStartsAt())
                .locationDTO(LocationDTO.builder()
                        .name(event.getLocation().getName())
                        .latitude(event.getLocation().getLatitude().doubleValue())
                        .longitude(event.getLocation().getLongitude().doubleValue())
                        .city(event.getLocation().getCity())
                        .country(event.getLocation().getCountry())
                        .formattedAddress(event.getLocation().getFormattedAddress())
                        .build())
                .sport(Sport.builder()
                        .category(event.getSportCategory())
                        .name(event.getSportName())
                        .build())
                .createdBy(event.getCreatedBy().getFirstname())
                .build();
    }
}
