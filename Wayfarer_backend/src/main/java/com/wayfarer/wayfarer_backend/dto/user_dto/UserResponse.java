package com.wayfarer.wayfarer_backend.dto.user_dto;

import java.time.Instant;

public record UserResponse(
        Integer id,
        String username,
        String email,
        Instant createdAt
) { }