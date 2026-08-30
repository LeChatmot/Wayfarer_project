package com.wayfarer.wayfarer_backend.dto.hike_dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record HikeCreateRequest(
        @NotBlank @Size(max = 60) String name,
        @Size(max = 2000) String description,
        boolean backToStart,
        @NotBlank String gpxContent
) {}
