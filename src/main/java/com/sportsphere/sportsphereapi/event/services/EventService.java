package com.sportsphere.sportsphereapi.event.services;

import com.sportsphere.sportsphereapi.event.DTO.EventDTO;
import com.sportsphere.sportsphereapi.event.entity.Event;
import com.sportsphere.sportsphereapi.event.entity.Location;
import com.sportsphere.sportsphereapi.event.mapper.EventMapper;
import com.sportsphere.sportsphereapi.event.repository.EventRepository;
import com.sportsphere.sportsphereapi.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {
    private final LocationService locationService;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Transactional
    public UUID createEvent(EventDTO eventDTO) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Location location = locationService.createLocation(eventDTO.getLocationDTO());
        Event event = eventMapper.toEntity(eventDTO, user, location);
        event = eventRepository.save(event);

        return event.getId();
    }

    public EventDTO getById(UUID id) {
        Optional<Event> event = eventRepository.findById(id);
        if (event.isPresent()) {
            return eventMapper.toDTO(event.get());
        }
        return null;
    }

    public List<EventDTO> getUpcomingEventsByLocation(double refLat, double refLon) {
        List<Event> events = eventRepository.findUpcomingEventsWithinDistance(refLat, refLon, 30000);
        return events.stream()
                .map(eventMapper::toDTO)
                .toList();
    }

    public List<EventDTO> getUpcomingEvents() {
        return eventRepository.findUpcomingEvents()
                .stream()
                .map(eventMapper::toDTO)
                .toList();
    }
}
