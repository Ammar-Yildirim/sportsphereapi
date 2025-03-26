package com.sportsphere.sportsphereapi.event.controller;

import com.sportsphere.sportsphereapi.event.DTO.EventDTO;
import com.sportsphere.sportsphereapi.event.entity.Event;
import com.sportsphere.sportsphereapi.event.mapper.EventMapper;
import com.sportsphere.sportsphereapi.event.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    @GetMapping("/getUpcomingEvents")
    public ResponseEntity<List<EventDTO>> getUpcomingEventsByLocation(@RequestParam(value = "refLat", required = false) Double refLat, @RequestParam(value = "refLon", required = false) Double refLon) {
        List<Event> events = eventService.getUpcomingEvents(refLat, refLon);
        return ResponseEntity.ok(mapEventsToDTOs(events));
    }

    @GetMapping("/getUpcomingEventsByCreator")
    public ResponseEntity<List<EventDTO>> getUpcomingEventsByCreator() {
        List<Event> events = eventService.getUpcomingEventsByCreator();
        return ResponseEntity.ok(mapEventsToDTOs(events));
    }

    @GetMapping("/getPastEventsByCreator")
    public ResponseEntity<List<EventDTO>> getPastEventsByCreator() {
        List<Event> events = eventService.getPastEventsByCreator();
        return ResponseEntity.ok(mapEventsToDTOs(events));
    }

    @GetMapping("/getUpcomingEventsByParticipant")
    public ResponseEntity<List<EventDTO>> getUpcomingEventsByParticipant() {
        List<Event> events = eventService.getUpcomingEventsByParticipant();
        return ResponseEntity.ok(mapEventsToDTOs(events));
    }

    @GetMapping("/getPastEventsByParticipant")
    public ResponseEntity<List<EventDTO>> getPastEventsByParticipant() {
        List<Event> events = eventService.getPastEventsByParticipant();
        return ResponseEntity.ok(mapEventsToDTOs(events));
    }

    @GetMapping("/getByID")
    public ResponseEntity<EventDTO> getById(@RequestParam("id") UUID id) {
        Event event = eventService.getById(id);
        if (event != null) return ResponseEntity.ok(eventMapper.toDTO(event));
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/create")
    public ResponseEntity<UUID> createEvent(@RequestBody EventDTO eventDTO) {
        UUID id = eventService.createEvent(eventDTO);
        return ResponseEntity.ok(id);
    }

    private List<EventDTO> mapEventsToDTOs(List<Event> events) {
        return events.stream()
                .map(eventMapper::toDTO)
                .toList();
    }
}
