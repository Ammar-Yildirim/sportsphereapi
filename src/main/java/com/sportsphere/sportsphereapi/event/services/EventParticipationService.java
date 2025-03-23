package com.sportsphere.sportsphereapi.event.services;

import com.sportsphere.sportsphereapi.event.DTO.EventParticipationCountDTO;
import com.sportsphere.sportsphereapi.event.DTO.request.EventParticipationRequest;
import com.sportsphere.sportsphereapi.event.DTO.response.EventParticipationResponse;
import com.sportsphere.sportsphereapi.event.entity.EventParticipation;
import com.sportsphere.sportsphereapi.event.mapper.EventParticipationMapper;
import com.sportsphere.sportsphereapi.event.repository.EventParticipationRepository;
import com.sportsphere.sportsphereapi.exception.CustomException;
import com.sportsphere.sportsphereapi.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;

    private final EventParticipationMapper eventParticipationMapper;

    public EventParticipationResponse addParticipation(EventParticipationRequest dto) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            EventParticipation eventParticipation = eventParticipationMapper.toEntity(dto, user);
            eventParticipationRepository.save(eventParticipation);
            return eventParticipationMapper.toEventParticipationResponse(eventParticipation);
        } catch (DataIntegrityViolationException ex) {
            throw new CustomException("Data Integrity Error", "Participant already exists", HttpStatus.CONFLICT);
        }
    }

    public List<EventParticipationResponse> getEventParticipation(UUID eventID) {
        return eventParticipationRepository.findByEventParticipationIDEventID(eventID)
                .stream().map(eventParticipationMapper::toEventParticipationResponse).toList();
    }

    public Map<UUID, Long> getParticipationCounts(List<UUID> eventIDs) {
        return eventParticipationRepository.getParticipationCounts(eventIDs).stream()
                .collect(Collectors.toMap(
                        EventParticipationCountDTO::getEventId,
                        EventParticipationCountDTO::getCount));
    }
}
