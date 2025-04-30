package com.sportsphere.sportsphereapi.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportsphere.sportsphereapi.event.DTO.MonthlyParticipationDTO;
import com.sportsphere.sportsphereapi.event.services.EventParticipationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EventParticipationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EventParticipationService eventParticipationService;

    @InjectMocks
    private EventParticipationController eventParticipationController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private UUID eventId;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventParticipationController).build();
        eventId = UUID.randomUUID();
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