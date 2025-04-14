package com.sportsphere.sportsphereapi.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportsphere.sportsphereapi.event.DTO.MonthlyParticipationDTO;
import com.sportsphere.sportsphereapi.event.DTO.request.EventParticipationRequest;
import com.sportsphere.sportsphereapi.event.DTO.response.EventParticipationResponse;
import com.sportsphere.sportsphereapi.event.entity.EventParticipation;
import com.sportsphere.sportsphereapi.event.mapper.EventParticipationMapper;
import com.sportsphere.sportsphereapi.event.services.EventParticipationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EventParticipationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EventParticipationService eventParticipationService;

    @Mock
    private EventParticipationMapper eventParticipationMapper;

    @InjectMocks
    private EventParticipationController eventParticipationController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private UUID eventId;
    private UUID userId;
    private EventParticipation eventParticipation;
    private EventParticipationResponse eventParticipationResponse;
    private EventParticipationRequest eventParticipationRequest;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(eventParticipationController).build();

        eventId = UUID.randomUUID();
        userId = UUID.randomUUID();

        eventParticipation = new EventParticipation();

        eventParticipationResponse = EventParticipationResponse.builder()
                .eventID(eventId)
                .userID(userId)
                .build();

        eventParticipationRequest = EventParticipationRequest.builder()
                .eventID(eventId)
                .build();
    }

    @Test
    void testAddParticipation() throws Exception {
        when(eventParticipationService.addParticipation(any(EventParticipationRequest.class)))
                .thenReturn(eventParticipation);
        when(eventParticipationMapper.toEventParticipationResponse(eventParticipation))
                .thenReturn(eventParticipationResponse);

        mockMvc.perform(post("/api/v1/eventParticipation/addParticipation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventParticipationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventID").value(eventId.toString()))
                .andExpect(jsonPath("$.userID").value(userId.toString()));
    }

    @Test
    void testRemoveParticipation() throws Exception {
        when(eventParticipationService.removeParticipation(eventId)).thenReturn(userId);

        mockMvc.perform(delete("/api/v1/eventParticipation/removeParticipation/" + eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(userId.toString()));
    }

    @Test
    void testGetEventParticipation() throws Exception {
        List<EventParticipation> participations = Arrays.asList(eventParticipation);

        when(eventParticipationService.getEventParticipation(eventId)).thenReturn(participations);
        when(eventParticipationMapper.toEventParticipationResponse(eventParticipation))
                .thenReturn(eventParticipationResponse);

        mockMvc.perform(get("/api/v1/eventParticipation/getEventParticipation")
                        .param("eventID", eventId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].eventID").value(eventId.toString()))
                .andExpect(jsonPath("$[0].userID").value(userId.toString()));
    }

    @Test
    void testGetParticipationCounts() throws Exception {
        List<UUID> eventIds = Arrays.asList(eventId);
        Map<UUID, Long> counts = new HashMap<>();
        counts.put(eventId, 5L);

        when(eventParticipationService.getParticipationCounts(eventIds)).thenReturn(counts);

        mockMvc.perform(post("/api/v1/eventParticipation/getParticipationCounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$." + eventId.toString()).value(5));
    }

    @Test
    void testGetMonthlyParticipation() throws Exception {
        MonthlyParticipationDTO dto = new MonthlyParticipationDTO(4, 10L);

        List<MonthlyParticipationDTO> monthlyData = Arrays.asList(dto);

        when(eventParticipationService.getMonthlyParticipation()).thenReturn(monthlyData);

        mockMvc.perform(get("/api/v1/eventParticipation/getMonthlyParticipation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].month").value("4"))
                .andExpect(jsonPath("$[0].participationCount").value(10));
    }
}