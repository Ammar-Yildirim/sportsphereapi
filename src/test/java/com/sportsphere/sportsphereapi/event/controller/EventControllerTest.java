package com.sportsphere.sportsphereapi.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EventService eventService;

    @Mock
    private CommentService commentService;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private EventParticipationService eventParticipationService;

    @Mock
    private EventParticipationMapper eventParticipationMapper;

    @InjectMocks
    private EventController eventController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private UUID eventId;
    private UUID userId;
    private Event event;
    private EventResponse eventResponse;
    private EventRequest eventRequest;
    private CommentRequest commentRequest;
    private CommentResponse commentResponse;
    private EventParticipation eventParticipation;
    private EventParticipationResponse eventParticipationResponse;
    private EventParticipationRequest eventParticipationRequest;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();

        eventId = UUID.randomUUID();
        userId = UUID.randomUUID();

        event = new Event();
        event.setId(eventId);

        eventResponse = EventResponse.builder()
                .id(eventId)
                .title("Test Event")
                .build();

        eventRequest = EventRequest.builder()
                .title("Test Event")
                .build();

        commentRequest = new CommentRequest("Test Comment");

        commentResponse = new CommentResponse();
        commentResponse.setId(1);
        commentResponse.setContent("Test Comment");

        eventParticipation = new EventParticipation();

        eventParticipationResponse = EventParticipationResponse.builder()
                .eventID(eventId)
                .userID(userId)
                .build();

        eventParticipationRequest = EventParticipationRequest.builder()
                .build();
    }

    @Test
    void testGetUpcomingEventsByLocation() throws Exception {
        List<Event> events = Arrays.asList(event);

        when(eventService.getUpcomingEvents(any(Double.class), any(Double.class))).thenReturn(events);
        when(eventMapper.toEventResponse(event)).thenReturn(eventResponse);

        mockMvc.perform(get("/api/v1/events/upcoming")
                        .param("refLat", "40.7128")
                        .param("refLon", "-74.0060"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(eventId.toString())));
    }

    @Test
    void testGetUpcomingEventsByCreator() throws Exception {
        List<Event> events = Arrays.asList(event);

        when(eventService.getUpcomingEventsByCreator()).thenReturn(events);
        when(eventMapper.toEventResponse(event)).thenReturn(eventResponse);

        mockMvc.perform(get("/api/v1/events/getUpcomingEventsByCreator"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(eventId.toString())));
    }

    @Test
    void testGetPastEventsByCreator() throws Exception {
        List<Event> events = Arrays.asList(event);

        when(eventService.getPastEventsByCreator()).thenReturn(events);
        when(eventMapper.toEventResponse(event)).thenReturn(eventResponse);

        mockMvc.perform(get("/api/v1/events/getPastEventsByCreator"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(eventId.toString())));
    }

    @Test
    void testGetUpcomingEventsByParticipant() throws Exception {
        List<Event> events = Arrays.asList(event);

        when(eventService.getUpcomingEventsByParticipant()).thenReturn(events);
        when(eventMapper.toEventResponse(event)).thenReturn(eventResponse);

        mockMvc.perform(get("/api/v1/events/getUpcomingEventsByParticipant"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(eventId.toString())));
    }

    @Test
    void testGetPastEventsByParticipant() throws Exception {
        List<Event> events = Arrays.asList(event);

        when(eventService.getPastEventsByParticipant()).thenReturn(events);
        when(eventMapper.toEventResponse(event)).thenReturn(eventResponse);

        mockMvc.perform(get("/api/v1/events/getPastEventsByParticipant"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(eventId.toString())));
    }

    @Test
    void testGetById() throws Exception {
        when(eventService.getById(eventId)).thenReturn(event);
        when(eventMapper.toEventResponse(event)).thenReturn(eventResponse);

        mockMvc.perform(get("/api/v1/events/" + eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(eventId.toString())));
    }

    @Test
    void testGetByIdNotFound() throws Exception {
        when(eventService.getById(eventId)).thenReturn(null);

        mockMvc.perform(get("/api/v1/events/" + eventId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateEvent() throws Exception {
        when(eventService.createEvent(any(EventRequest.class))).thenReturn(eventId);

        mockMvc.perform(post("/api/v1/events/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(eventId.toString())));
    }

    @Test
    void testDeleteEvent() throws Exception {
        when(eventService.deleteEvent(eventId)).thenReturn(eventId);

        mockMvc.perform(delete("/api/v1/events/" + eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(eventId.toString())));
    }

    @Test
    void testGetCommentsByEventId() throws Exception {
        List<CommentResponse> commentResponses = Arrays.asList(commentResponse);
        when(commentService.getCommentsByEventId(eventId)).thenReturn(commentResponses);

        mockMvc.perform(get("/api/v1/events/" + eventId + "/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void testCreateComment() throws Exception {
        when(commentService.createComment(eq(eventId), any(CommentRequest.class))).thenReturn(commentResponse);

        mockMvc.perform(post("/api/v1/events/" + eventId + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void testGetEventParticipation() throws Exception {
        List<EventParticipation> participations = Arrays.asList(eventParticipation);

        when(eventParticipationService.getEventParticipation(eventId)).thenReturn(participations);
        when(eventParticipationMapper.toEventParticipationResponse(eventParticipation))
                .thenReturn(eventParticipationResponse);

        mockMvc.perform(get("/api/v1/events/" + eventId + "/participation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].eventID", is(eventId.toString())))
                .andExpect(jsonPath("$[0].userID", is(userId.toString())));
    }

    @Test
    void testAddParticipation() throws Exception {
        when(eventParticipationService.addParticipation(eq(eventId), any(EventParticipationRequest.class)))
                .thenReturn(eventParticipation);
        when(eventParticipationMapper.toEventParticipationResponse(eventParticipation))
                .thenReturn(eventParticipationResponse);

        mockMvc.perform(post("/api/v1/events/" + eventId + "/participation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventParticipationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventID", is(eventId.toString())))
                .andExpect(jsonPath("$.userID", is(userId.toString())));
    }

    @Test
    void testRemoveParticipation() throws Exception {
        when(eventParticipationService.removeParticipation(eventId)).thenReturn(userId);

        mockMvc.perform(delete("/api/v1/events/" + eventId + "/participation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(userId.toString())));
    }
}