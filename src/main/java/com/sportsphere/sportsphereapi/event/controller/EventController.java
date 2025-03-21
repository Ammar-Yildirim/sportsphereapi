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

    @GetMapping("/getUpcomingEventsByLocation")
    public ResponseEntity<List<EventDTO>> getUpcomingEventsByLocation(@RequestParam("refLat") double refLat, @RequestParam("refLon") double refLon) {
        List<EventDTO> eventDTOs = eventService.getUpcomingEventsByLocation(refLat, refLon);
        return ResponseEntity.ok(eventDTOs);
    }

    @GetMapping("/getUpcomingEvents")
    public ResponseEntity<List<EventDTO>> getUpcomingEvents() {
        List<EventDTO> eventDTOs = eventService.getUpcomingEvents();
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
