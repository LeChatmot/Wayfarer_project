package com.wayfarer.wayfarer_backend.dto.hike_dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record HikeUpdateRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 2000) String description
) {
}
