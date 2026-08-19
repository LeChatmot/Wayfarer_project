package com.wayfarer.wayfarer_backend.dto.auth_dto;

public record AuthResponse(String token, String username, String email) {}