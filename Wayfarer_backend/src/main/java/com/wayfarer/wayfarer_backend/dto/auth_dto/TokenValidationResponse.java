package com.wayfarer.wayfarer_backend.dto.auth_dto;

import jakarta.validation.constraints.NotBlank;

public class TokenValidationResponse {
    @NotBlank boolean valid;
    @NotBlank boolean refreshable;

    public TokenValidationResponse(boolean valid, boolean refreshable){
        this.valid = valid;
        this.refreshable = refreshable;
    }
}
