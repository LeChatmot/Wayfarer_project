package com.wayfarer.wayfarer_backend.mapper;

import com.wayfarer.wayfarer_backend.dto.user_dto.UserResponse;
import com.wayfarer.wayfarer_backend.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}