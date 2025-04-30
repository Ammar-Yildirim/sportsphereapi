package com.sportsphere.sportsphereapi.event.controller;

import com.sportsphere.sportsphereapi.event.DTO.request.CommentRequest;
import com.sportsphere.sportsphereapi.event.DTO.request.EventParticipationRequest;
import com.sportsphere.sportsphereapi.event.DTO.request.EventRequest;
import com.sportsphere.sportsphereapi.event.DTO.response.CommentResponse;
import com.sportsphere.sportsphereapi.event.DTO.response.EventParticipationResponse;
import com.sportsphere.sportsphereapi.event.DTO.response.EventResponse;
import com.sportsphere.sportsphereapi.event.entity.Event;
import com.sportsphere.sportsphereapi.event.entity.EventParticipation;
import com.sportsphere.sportsphereapi.event.mapper.EventMapper;
import com.sportsphere.sportsphereapi.event.mapper.EventParticipationMapper;
import com.sportsphere.sportsphereapi.event.services.CommentService;
import com.sportsphere.sportsphereapi.event.services.EventParticipationService;
import com.sportsphere.sportsphereapi.event.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;
    private final CommentService commentService;
    private final EventMapper eventMapper;
    private final EventParticipationService eventParticipationService;
    private final EventParticipationMapper eventParticipationMapper;

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

    @GetMapping("/{eventId}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentsByEventId(@PathVariable UUID eventId) {
        List<CommentResponse> comments = commentService.getCommentsByEventId(eventId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/{eventId}/comments")
    public ResponseEntity<CommentResponse> createComment(@PathVariable UUID eventId, @RequestBody CommentRequest request) {
        CommentResponse response = commentService.createComment(eventId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{eventId}/participation")
    public ResponseEntity<List<EventParticipationResponse>> getEventParticipation(@PathVariable("eventId") UUID eventId) {
        List<EventParticipation> events = eventParticipationService.getEventParticipation(eventId);
        return ResponseEntity.ok(events.stream()
                .map(eventParticipationMapper::toEventParticipationResponse)
                .toList());
    }

    @PostMapping("/{eventId}/participation")
    public ResponseEntity<EventParticipationResponse> addParticipation(@PathVariable("eventId") UUID eventId, @RequestBody EventParticipationRequest request) {
        EventParticipation eventParticipation = eventParticipationService.addParticipation(eventId, request);
        return ResponseEntity.ok(eventParticipationMapper.toEventParticipationResponse(eventParticipation));
    }

    @DeleteMapping("/{eventId}/participation")
    public ResponseEntity<UUID> removeParticipation(@PathVariable("eventId") UUID eventId) {
        UUID removedUserId = eventParticipationService.removeParticipation(eventId);
        return ResponseEntity.ok(removedUserId);
    }

    private List<EventResponse> mapEventsToEventResponses(List<Event> events) {
        return events.stream()
                .map(eventMapper::toEventResponse)
                .toList();
    }
}
