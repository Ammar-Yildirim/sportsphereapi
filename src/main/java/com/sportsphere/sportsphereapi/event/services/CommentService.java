package com.sportsphere.sportsphereapi.event.services;

import com.sportsphere.sportsphereapi.event.DTO.request.CommentRequest;
import com.sportsphere.sportsphereapi.event.DTO.response.CommentResponse;
import com.sportsphere.sportsphereapi.event.entity.Comment;
import com.sportsphere.sportsphereapi.event.entity.Event;
import com.sportsphere.sportsphereapi.event.entity.EventParticipation;
import com.sportsphere.sportsphereapi.event.mapper.CommentMapper;
import com.sportsphere.sportsphereapi.event.repository.CommentRepository;
import com.sportsphere.sportsphereapi.event.repository.EventParticipationRepository;
import com.sportsphere.sportsphereapi.event.repository.EventRepository;
import com.sportsphere.sportsphereapi.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final EventParticipationRepository eventParticipationRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentResponse createComment(UUID eventId, CommentRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        if (event.getStartsAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("You cannot comment on an event that has already started.");
        }

        boolean isParticipant = eventParticipationRepository
                .findByEventParticipationIDUserIDAndEventParticipationIDEventID(user.getId(), eventId)
                .isPresent();
        if (!isParticipant) {
            throw new IllegalArgumentException("You must be a participant to comment on this event.");
        }

        Comment comment = Comment.builder()
                .event(event)
                .user(user)
                .content(request.getContent())
                .build();
        comment = commentRepository.save(comment);

        return commentMapper.mapToResponse(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByEventId(UUID eventId) {
        return commentRepository.findByEventIdOrderByCreatedAtDesc(eventId)
                .stream()
                .map(commentMapper::mapToResponse)
                .toList();
    }
}