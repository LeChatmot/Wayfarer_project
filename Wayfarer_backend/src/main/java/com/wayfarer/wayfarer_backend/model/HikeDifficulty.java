package com.wayfarer.wayfarer_backend.model;

import lombok.Getter;

@Getter
public enum HikeDifficulty {
    EASY("Facile"),
    MEDIUM("Moyen"),
    HARD("Difficile"),
    VERY_HARD("Très Difficile");

    private final String title;

    HikeDifficulty(String title){
        this.title = title;
    }
}
