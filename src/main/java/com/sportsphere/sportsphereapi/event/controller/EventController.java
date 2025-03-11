package com.sportsphere.sportsphereapi.event.controller;

import com.sportsphere.sportsphereapi.event.Entity.Event;
import com.sportsphere.sportsphereapi.event.mapper.EventMapper;
import com.sportsphere.sportsphereapi.event.repository.EventRepository;
import com.sportsphere.sportsphereapi.event.DTO.EventDTO;
import com.sportsphere.sportsphereapi.event.DTO.Location;
import com.sportsphere.sportsphereapi.event.DTO.Sport;
import com.sportsphere.sportsphereapi.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @GetMapping("/getAll")
    public ResponseEntity<List<EventDTO>> getAll(){
        List<Event> events = eventRepository.findAll();
        List<EventDTO> eventDTOs = events.stream()
                .map(eventMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(eventDTOs);
    }

    @GetMapping("/getByID")
    public ResponseEntity<EventDTO> getById(@RequestParam("id") Integer id){
        Optional<Event> event = eventRepository.findById(id);
        if(event.isPresent()){
            EventDTO eventDTO = eventMapper.toDTO(event.get());
            return ResponseEntity.ok(eventDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/create")
    public ResponseEntity<String> createEvent(@RequestBody EventDTO eventDTO) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Event event = eventMapper.toEntity(eventDTO, user);
        eventRepository.save(event);

        return ResponseEntity.ok("saved");
    }
}
