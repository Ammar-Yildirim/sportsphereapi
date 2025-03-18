package com.sportsphere.sportsphereapi.event.controller;

import com.sportsphere.sportsphereapi.event.DTO.EventParticipationDTO;
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
    public ResponseEntity<String> addParticipation(@RequestBody EventParticipationDTO dto) {
        eventParticipationService.addParticipation(dto);

        return ResponseEntity.ok("ok");
    }

    @GetMapping("/getEventParticipation")
    public ResponseEntity<List<EventParticipationDTO>> getEventParticipation(@RequestParam
                                                                             UUID eventID) {
        return ResponseEntity.ok(eventParticipationService.getEventParticipation(eventID));
    }
}
