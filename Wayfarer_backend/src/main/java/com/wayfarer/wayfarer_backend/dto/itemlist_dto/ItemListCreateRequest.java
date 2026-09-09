package com.wayfarer.wayfarer_backend.dto.itemlist_dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ItemListCreateRequest(
        @NotBlank @Size(max = 120) String name
) {
}
