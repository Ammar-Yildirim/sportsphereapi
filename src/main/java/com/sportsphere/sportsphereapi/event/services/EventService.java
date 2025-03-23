package com.sportsphere.sportsphereapi.event.services;

import com.sportsphere.sportsphereapi.event.DTO.EventDTO;
import com.sportsphere.sportsphereapi.event.entity.Event;
import com.sportsphere.sportsphereapi.event.entity.Location;
import com.sportsphere.sportsphereapi.event.mapper.EventMapper;
import com.sportsphere.sportsphereapi.event.repository.EventRepository;
import com.sportsphere.sportsphereapi.exception.CustomException;
import com.sportsphere.sportsphereapi.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
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
        return eventRepository.findById(id)
                .map(eventMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with ID: " + id));
    }

    public List<EventDTO> getUpcomingEvents(Double refLat, Double refLon) {
        List<Event> events = (refLat != null && refLon != null)
                ? eventRepository.findUpcomingEventsWithinDistance(refLat, refLon, 30000)
                : eventRepository.findUpcomingEvents();

        return events.stream()
                .map(eventMapper::toDTO)
                .toList();
    }

    public List<EventDTO> getUpcomingEventsByCreator() {
        try{
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return eventRepository.findUpcomingEventsByCreator(user.getId()).stream()
                    .map(eventMapper::toDTO)
                    .toList();
        }catch (Exception e){
            throw new CustomException("Database Error", "Error occurred while fetching Upcoming Events", HttpStatus.NOT_FOUND);
        }
    }

    public List<EventDTO> getPastEventsByCreator() {
        try{
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return eventRepository.findPastEventsByCreator(user.getId()).stream()
                    .map(eventMapper::toDTO)
                    .toList();
        }catch (Exception e){
            throw new CustomException("Database Error", "Error occurred while fetching Past Events", HttpStatus.NOT_FOUND);
        }
    }

    public List<EventDTO> getUpcomingEventsByParticipant() {
        try{
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return eventRepository.findUpcomingEventsByParticipant(user.getId()).stream()
                    .map(eventMapper::toDTO)
                    .toList();
        }catch (Exception e){
            throw new CustomException("Database Error", "Error occurred while fetching Upcoming Events", HttpStatus.NOT_FOUND);
        }
    }

    public List<EventDTO> getPastEventsByParticipant() {
        try{
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return eventRepository.findPastEventsByParticipant(user.getId()).stream()
                    .map(eventMapper::toDTO)
                    .toList();
        }catch (Exception e){
            throw new CustomException("Database Error", "Error occurred while fetching Past Events", HttpStatus.NOT_FOUND);
        }
    }
}
