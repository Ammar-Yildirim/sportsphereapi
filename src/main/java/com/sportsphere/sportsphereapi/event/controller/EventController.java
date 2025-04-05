package com.sportsphere.sportsphereapi.event.controller;

import com.sportsphere.sportsphereapi.event.DTO.request.EventRequest;
import com.sportsphere.sportsphereapi.event.DTO.response.EventResponse;
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

    @GetMapping("/upcoming")
    public ResponseEntity<List<EventResponse>> getUpcomingEventsByLocation(@RequestParam(value = "refLat", required = false) Double refLat, @RequestParam(value = "refLon", required = false) Double refLon) {
        List<Event> events = eventService.getUpcomingEvents(refLat, refLon);
        return ResponseEntity.ok(mapEventsToEventResponses(events));
    }

    @GetMapping("/getUpcomingEventsByCreator")
    public ResponseEntity<List<EventResponse>> getUpcomingEventsByCreator() {
        List<Event> events = eventService.getUpcomingEventsByCreator();
        return ResponseEntity.ok(mapEventsToEventResponses(events));
    }

    @GetMapping("/getPastEventsByCreator")
    public ResponseEntity<List<EventResponse>> getPastEventsByCreator() {
        List<Event> events = eventService.getPastEventsByCreator();
        return ResponseEntity.ok(mapEventsToEventResponses(events));
    }

    @GetMapping("/getUpcomingEventsByParticipant")
    public ResponseEntity<List<EventResponse>> getUpcomingEventsByParticipant() {
        List<Event> events = eventService.getUpcomingEventsByParticipant();
        return ResponseEntity.ok(mapEventsToEventResponses(events));
    }

    @GetMapping("/getPastEventsByParticipant")
    public ResponseEntity<List<EventResponse>> getPastEventsByParticipant() {
        List<Event> events = eventService.getPastEventsByParticipant();
        return ResponseEntity.ok(mapEventsToEventResponses(events));
    }

    @GetMapping("/{eventID}")
    public ResponseEntity<EventResponse> getById(@PathVariable("eventID") UUID eventID) {
        Event event = eventService.getById(eventID);
        if (event != null) return ResponseEntity.ok(eventMapper.toEventResponse(event));
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/create")
    public ResponseEntity<UUID> createEvent(@RequestBody EventRequest eventRequest) {
        UUID id = eventService.createEvent(eventRequest);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{eventID}")
    public ResponseEntity<UUID> deleteEvent(@PathVariable("eventID") UUID eventID) {
        UUID id = eventService.deleteEvent(eventID);
        return ResponseEntity.ok(id);
    }

    private List<EventResponse> mapEventsToEventResponses(List<Event> events) {
        return events.stream()
                .map(eventMapper::toEventResponse)
                .toList();
    }
}
