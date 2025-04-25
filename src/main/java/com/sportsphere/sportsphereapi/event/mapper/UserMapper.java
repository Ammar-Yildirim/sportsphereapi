package com.sportsphere.sportsphereapi.event.mapper;

import com.sportsphere.sportsphereapi.event.DTO.response.UserResponse;
import com.sportsphere.sportsphereapi.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .enabled(user.getEnabled())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
