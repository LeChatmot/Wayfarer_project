package com.wayfarer.wayfarer_backend.dto.auth_dto;

import lombok.Getter;

@Getter
public class AuthResponse {

    private String accessToken;
    private String refreshToken;

    public AuthResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}