package com.sportsphere.sportsphereapi.event.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private UUID id;
    private UUID eventId;
    private UUID userId;
    private String userFirstName;
    private String userLastName;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}