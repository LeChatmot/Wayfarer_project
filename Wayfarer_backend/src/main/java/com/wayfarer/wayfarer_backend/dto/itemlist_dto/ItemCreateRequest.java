package com.wayfarer.wayfarer_backend.dto.itemlist_dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ItemCreateRequest(
        @NotBlank @Size(max = 60) String name,
        @PositiveOrZero Integer quantity
) {
}
