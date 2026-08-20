package com.wayfarer.wayfarer_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Entity
@Table(name = "hikes")
public class Hike extends BaseEntity{

    @Getter
    @Setter
    @Column(nullable = false, length = 60)
    private String name;

    @Getter
    @Setter
    @Column(name = "id_creator", nullable = false)
    private Integer creatorId;

    @Getter
    @Setter
    @Column(nullable = false, columnDefinition = "geometry(Point,4326)")
    private Point startingPoint;  // Champ géométrique PostGIS

    @Getter
    @Setter
    @Column(name = "back_to_start", nullable = false)
    private boolean backToStart = false;

    @Getter
    @Setter
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Getter
    @Setter
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
