package com.sportsphere.sportsphereapi.event.controller;

import com.sportsphere.sportsphereapi.event.DTO.response.EventResponse;
import com.sportsphere.sportsphereapi.event.DTO.response.UserResponse;
import com.sportsphere.sportsphereapi.event.mapper.EventMapper;
import com.sportsphere.sportsphereapi.event.mapper.UserMapper;
import com.sportsphere.sportsphereapi.event.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;
    private final UserMapper userMapper;
    private final EventMapper eventMapper;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers(){
        return ResponseEntity.ok(adminService.getUsers()
                .stream()
                .map(userMapper::toResponse)
                .toList());
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<UUID> deleteUser(@PathVariable("userId") UUID userId){
        adminService.deleteUser(userId);
        return ResponseEntity.ok(userId);
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventResponse>> getEvents(){
        return ResponseEntity.ok(adminService.getEvents()
                .stream()
                .map(eventMapper::toEventResponse)
                .toList());
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<UUID> deleteEvent(@PathVariable("eventId") UUID eventId){
        adminService.deleteEvent(eventId);
        return ResponseEntity.ok(eventId);
    }
}
