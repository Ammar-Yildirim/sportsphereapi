package com.sportsphere.sportsphereapi.event.controller;

import com.sportsphere.sportsphereapi.event.DTO.EventDTO;
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

    @GetMapping("/getUpcomingEvents")
    public ResponseEntity<List<EventDTO>> getUpcomingEventsByLocation(@RequestParam(value = "refLat", required = false) Double refLat, @RequestParam(value = "refLon", required = false) Double refLon) {
        List<EventDTO> eventDTOs = eventService.getUpcomingEvents(refLat, refLon);
        return ResponseEntity.ok(eventDTOs);
    }

    @GetMapping("/getUpcomingEventsByCreator")
    public ResponseEntity<List<EventDTO>> getUpcomingEventsByCreator() {
        List<EventDTO> eventDTOs = eventService.getUpcomingEventsByCreator();
        return ResponseEntity.ok(eventDTOs);
    }

    @GetMapping("/getPastEventsByCreator")
    public ResponseEntity<List<EventDTO>> getPastEventsByCreator() {
        List<EventDTO> eventDTOs = eventService.getPastEventsByCreator();
        return ResponseEntity.ok(eventDTOs);
    }

    @GetMapping("/getUpcomingEventsByParticipant")
    public ResponseEntity<List<EventDTO>> getUpcomingEventsByParticipant() {
        List<EventDTO> eventDTOs = eventService.getUpcomingEventsByParticipant();
        return ResponseEntity.ok(eventDTOs);
    }

    @GetMapping("/getPastEventsByParticipant")
    public ResponseEntity<List<EventDTO>> getPastEventsByParticipant() {
        List<EventDTO> eventDTOs = eventService.getPastEventsByParticipant();
        return ResponseEntity.ok(eventDTOs);
    }

    @GetMapping("/getByID")
    public ResponseEntity<EventDTO> getById(@RequestParam("id") UUID id) {
        EventDTO eventDTO = eventService.getById(id);
        if (eventDTO != null) return ResponseEntity.ok(eventDTO);
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/create")
    public ResponseEntity<UUID> createEvent(@RequestBody EventDTO eventDTO) {
        UUID id = eventService.createEvent(eventDTO);
        return ResponseEntity.ok(id);
    }
}
