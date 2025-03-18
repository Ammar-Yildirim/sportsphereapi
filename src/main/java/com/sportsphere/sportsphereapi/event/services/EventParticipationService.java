package com.sportsphere.sportsphereapi.event.services;

import com.sportsphere.sportsphereapi.event.DTO.EventParticipationDTO;
import com.sportsphere.sportsphereapi.event.entity.EventParticipation;
import com.sportsphere.sportsphereapi.event.mapper.EventParticipationMapper;
import com.sportsphere.sportsphereapi.event.repository.EventParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;

    private final EventParticipationMapper eventParticipationMapper;

    public void addParticipation(EventParticipationDTO dto) {
        EventParticipation eventParticipation = eventParticipationMapper.toEntity(dto);
        eventParticipationRepository.save(eventParticipation);
    }

    public List<EventParticipationDTO> getEventParticipation(UUID eventID) {
        return eventParticipationRepository.findByEventParticipationIDEventID(eventID)
                .stream().map(eventParticipationMapper::toDTO).toList();
    }
}
