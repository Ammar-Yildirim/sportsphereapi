package com.sportsphere.sportsphereapi.event.controller;

import com.sportsphere.sportsphereapi.event.DTO.MonthlyParticipationDTO;
import com.sportsphere.sportsphereapi.event.DTO.request.EventParticipationRequest;
import com.sportsphere.sportsphereapi.event.DTO.response.EventParticipationResponse;
import com.sportsphere.sportsphereapi.event.entity.EventParticipation;
import com.sportsphere.sportsphereapi.event.mapper.EventParticipationMapper;
import com.sportsphere.sportsphereapi.event.services.EventParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/eventParticipation")
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;
    private final EventParticipationMapper eventParticipationMapper;

    @PostMapping("/addParticipation")
    public ResponseEntity<EventParticipationResponse> addParticipation(@RequestBody EventParticipationRequest request) {
        EventParticipation eventParticipation = eventParticipationService.addParticipation(request);
        return ResponseEntity.ok(eventParticipationMapper.toEventParticipationResponse(eventParticipation));
    }

    @DeleteMapping("/removeParticipation/{eventId}")
    public ResponseEntity<UUID> removeParticipation(@PathVariable("eventId") UUID eventId) {
        UUID removedUserId = eventParticipationService.removeParticipation(eventId);
        return ResponseEntity.ok(removedUserId);
    }

    @GetMapping("/getEventParticipation")
    public ResponseEntity<List<EventParticipationResponse>> getEventParticipation(@RequestParam
                                                                                  UUID eventID) {
        List<EventParticipation> events = eventParticipationService.getEventParticipation(eventID);
        return ResponseEntity.ok(events.stream()
                .map(eventParticipationMapper::toEventParticipationResponse)
                .toList());
    }

    @PostMapping("/getParticipationCounts")
    public ResponseEntity<Map<UUID, Long>> getParticipationCounts(@RequestBody List<UUID> eventIDs) {
        return ResponseEntity.ok(eventParticipationService.getParticipationCounts(eventIDs));
    }

    @GetMapping("/getMonthlyParticipation")
    public ResponseEntity<List<MonthlyParticipationDTO>> getMonthlyParticipation() {
        return ResponseEntity.ok(eventParticipationService.getMonthlyParticipation());
    }
}
