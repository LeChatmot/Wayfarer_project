package com.wayfarer.wayfarer_backend.dto.user_dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @NotBlank @Size(min = 3, max = 60) String username,
        @NotBlank @Email String email
) { }