package com.sportsphere.sportsphereapi.event.mapper;

import com.sportsphere.sportsphereapi.event.DTO.response.CommentResponse;
import com.sportsphere.sportsphereapi.event.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public CommentResponse mapToResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getEvent().getId(),
                comment.getUser().getId(),
                comment.getUser().getFirstname(),
                comment.getUser().getLastname(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
