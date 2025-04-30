package com.sportsphere.sportsphereapi.event.DTO.response;

import com.sportsphere.sportsphereapi.user.Role;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private UUID id;
    private String firstname;
    private String lastname;
    private String email;
    private Boolean enabled;
    private Role role;
    private LocalDateTime createdAt;
}
