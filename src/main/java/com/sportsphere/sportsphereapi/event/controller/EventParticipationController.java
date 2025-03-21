package com.sportsphere.sportsphereapi.event.controller;

import com.sportsphere.sportsphereapi.event.DTO.request.EventParticipationRequest;
import com.sportsphere.sportsphereapi.event.DTO.response.EventParticipationResponse;
import com.sportsphere.sportsphereapi.event.entity.EventParticipation;
import com.sportsphere.sportsphereapi.event.services.EventParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/eventParticipation")
public class EventParticipationController {

    private final EventParticipationService eventParticipationService;

    @PostMapping("/addParticipation")
    public ResponseEntity<EventParticipationResponse> addParticipation(@RequestBody EventParticipationRequest request) {
        EventParticipationResponse eventParticipationResponse =  eventParticipationService.addParticipation(request);

        return ResponseEntity.ok(eventParticipationResponse);
    }

    @GetMapping("/getEventParticipation")
    public ResponseEntity<List<EventParticipationResponse>> getEventParticipation(@RequestParam
                                                                             UUID eventID) {
        List<EventParticipationResponse> events = eventParticipationService.getEventParticipation(eventID);
        return ResponseEntity.ok(events);
    }
}
