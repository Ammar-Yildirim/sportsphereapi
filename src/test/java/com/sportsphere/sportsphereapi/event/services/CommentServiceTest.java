package com.sportsphere.sportsphereapi.event.services;

import com.sportsphere.sportsphereapi.event.DTO.request.CommentRequest;
import com.sportsphere.sportsphereapi.event.DTO.response.CommentResponse;
import com.sportsphere.sportsphereapi.event.entity.Comment;
import com.sportsphere.sportsphereapi.event.entity.Event;
import com.sportsphere.sportsphereapi.event.entity.EventParticipation;
import com.sportsphere.sportsphereapi.event.entity.ID.EventParticipationID;
import com.sportsphere.sportsphereapi.event.mapper.CommentMapper;
import com.sportsphere.sportsphereapi.event.repository.CommentRepository;
import com.sportsphere.sportsphereapi.event.repository.EventParticipationRepository;
import com.sportsphere.sportsphereapi.event.repository.EventRepository;
import com.sportsphere.sportsphereapi.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private CommentService commentService;

    private User testUser;
    private Event testEvent;
    private Comment testComment;
    private CommentRequest testRequest;
    private CommentResponse testResponse;
    private UUID eventId;
    private UUID userId;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        eventId = UUID.randomUUID();

        testUser = User.builder()
                .id(userId)
                .email("test@example.com")
                .firstname("John")
                .lastname("Doe")
                .build();

        testEvent = Event.builder()
                .id(eventId)
                .title("Test Event")
                .description("Test Description")
                .startsAt(LocalDateTime.now().plusDays(1))
                .sportCategory("Test Category")
                .sportName("Test Sport")
                .build();

        testComment = Comment.builder()
                .id(1)
                .event(testEvent)
                .user(testUser)
                .content("Test comment content")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testRequest = new CommentRequest("Test comment content");

        testResponse = new CommentResponse();
        testResponse.setId(1);
        testResponse.setEventId(eventId);
        testResponse.setUserId(userId);
        testResponse.setUserFirstName("John");
        testResponse.setUserLastName("Doe");
        testResponse.setContent("Test comment content");
        testResponse.setCreatedAt(LocalDateTime.now());
        testResponse.setUpdatedAt(LocalDateTime.now());

        authentication = new UsernamePasswordAuthenticationToken(testUser, null);
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    void testCreateComment_WhenValidInput_ReturnsSuccess() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));

        EventParticipationID participationID = EventParticipationID.builder()
                .eventID(eventId)
                .userID(userId)
                .build();

        EventParticipation participation = EventParticipation.builder()
                .eventParticipationID(participationID)
                .build();

        when(eventParticipationRepository.findByEventParticipationIDUserIDAndEventParticipationIDEventID(userId, eventId))
                .thenReturn(Optional.of(participation));

        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
        when(commentMapper.mapToResponse(testComment)).thenReturn(testResponse);

        CommentResponse response = commentService.createComment(eventId, testRequest);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals(eventId, response.getEventId());
        assertEquals(userId, response.getUserId());
        assertEquals("Test comment content", response.getContent());

        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(commentMapper, times(1)).mapToResponse(any(Comment.class));
    }

    @Test
    void testCreateComment_WhenEventNotFound_ThrowsException() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commentService.createComment(eventId, testRequest)
        );

        assertEquals("Event not found", exception.getMessage());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void testCreateComment_WhenEventAlreadyStarted_ThrowsException() {
        Event pastEvent = Event.builder()
                .id(eventId)
                .title("Past Event")
                .description("Past Description")
                .startsAt(LocalDateTime.now().minusDays(1))
                .build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(pastEvent));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commentService.createComment(eventId, testRequest)
        );

        assertEquals("You cannot comment on an event that has already started.", exception.getMessage());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void testCreateComment_WhenUserNotParticipant_ThrowsException() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));
        when(eventParticipationRepository.findByEventParticipationIDUserIDAndEventParticipationIDEventID(userId, eventId))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commentService.createComment(eventId, testRequest)
        );

        assertEquals("You must be a participant to comment on this event.", exception.getMessage());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void testGetCommentsByEventId_WhenEventExists_ReturnsComments() {
        Comment anotherComment = Comment.builder()
                .id(2)
                .event(testEvent)
                .user(testUser)
                .content("Another comment")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<Comment> comments = Arrays.asList(testComment, anotherComment);

        CommentResponse anotherResponse = new CommentResponse();
        anotherResponse.setId(2);
        anotherResponse.setContent("Another comment");

        when(commentRepository.findByEventIdOrderByCreatedAtDesc(eventId)).thenReturn(comments);
        when(commentMapper.mapToResponse(testComment)).thenReturn(testResponse);
        when(commentMapper.mapToResponse(anotherComment)).thenReturn(anotherResponse);

        List<CommentResponse> responses = commentService.getCommentsByEventId(eventId);

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(1, responses.get(0).getId());
        assertEquals(2, responses.get(1).getId());

        verify(commentRepository, times(1)).findByEventIdOrderByCreatedAtDesc(eventId);
        verify(commentMapper, times(2)).mapToResponse(any(Comment.class));
    }
}