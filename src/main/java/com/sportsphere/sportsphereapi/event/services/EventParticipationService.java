package com.sportsphere.sportsphereapi.event.services;

import com.sportsphere.sportsphereapi.event.DTO.EventParticipationCountDTO;
import com.sportsphere.sportsphereapi.event.DTO.MonthlyParticipationDTO;
import com.sportsphere.sportsphereapi.event.DTO.request.EventParticipationRequest;
import com.sportsphere.sportsphereapi.event.entity.Event;
import com.sportsphere.sportsphereapi.event.entity.EventParticipation;
import com.sportsphere.sportsphereapi.event.entity.ID.EventParticipationID;
import com.sportsphere.sportsphereapi.event.mapper.EventParticipationMapper;
import com.sportsphere.sportsphereapi.event.repository.EventParticipationRepository;
import com.sportsphere.sportsphereapi.exception.CustomException;
import com.sportsphere.sportsphereapi.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;
    private final EventService eventService;
    private final EventParticipationMapper eventParticipationMapper;

    @Transactional
    public EventParticipation addParticipation(EventParticipationRequest dto) {
        Event event = eventService.getById(dto.getEventID());
        if (event.getStartsAt().isBefore(LocalDateTime.now())) {
            throw new CustomException("Bad request", "You can't participate in a past event.", HttpStatus.BAD_REQUEST);
        }

        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            EventParticipation eventParticipation = eventParticipationMapper.toEntity(dto, user);
            eventParticipationRepository.save(eventParticipation);
            return eventParticipation;
        } catch (DataIntegrityViolationException ex) {
            throw new CustomException("Data Integrity Error", "This spot was occupied, please try another.", HttpStatus.CONFLICT);
        }
    }

    @Transactional
    public UUID removeParticipation(UUID eventId) {
        Event event = eventService.getById(eventId);
        LocalDateTime now = LocalDateTime.now();

        if (event.getStartsAt().isBefore(now)) {
            throw new CustomException("Bad request", "You can't remove participation in a past event.", HttpStatus.BAD_REQUEST);
        }

        if (event.getStartsAt().isBefore(now.plusMinutes(30))) {
            throw new CustomException("Bad request", "You can't leave an event with 30 minutes or less left until it starts.", HttpStatus.BAD_REQUEST);
        }

        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UUID userId = user.getId();
            eventParticipationRepository.deleteById(EventParticipationID.builder()
                    .eventID(eventId)
                    .userID(userId)
                    .build());
            return userId;
        } catch (DataIntegrityViolationException ex) {
            throw new CustomException("Database Error", "An error occurred while removing participation.", HttpStatus.CONFLICT);
        }
    }

    public List<EventParticipation> getEventParticipation(UUID eventID) {
        return eventParticipationRepository.findByEventParticipationIDEventID(eventID);
    }

    public Map<UUID, Long> getParticipationCounts(List<UUID> eventIDs) {
        return eventParticipationRepository.getParticipationCounts(eventIDs).stream()
                .collect(Collectors.toMap(
                        EventParticipationCountDTO::getEventId,
                        EventParticipationCountDTO::getCount));
    }

    public List<MonthlyParticipationDTO> getMonthlyParticipation() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return eventParticipationRepository.getMonthlyParticipation(user.getId());
    }
}
