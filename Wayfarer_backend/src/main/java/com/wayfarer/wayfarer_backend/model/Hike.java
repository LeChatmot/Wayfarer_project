package com.wayfarer.wayfarer_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Entity
@Table(name = "hikes")
public class Hike extends BaseEntity {

    @Setter
    @Getter
    @Column(nullable = false, length = 60)
    private String name;

    @Setter
    @Getter
    @Column(columnDefinition = "TEXT")
    private String description;

    @Setter
    @Getter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_creator", nullable = false)
    private User creator;

    @Setter
    @Getter
    @Column(name = "starting_point", nullable = false, columnDefinition = "geometry(POINTZ,4326)")
    private Point startingPoint;

    @Setter
    @Getter
    @Column(nullable = false, columnDefinition = "geometry(LineStringZ,4326)")
    private LineString path;

    @Setter
    @Getter
    @Column(name = "back_to_start", nullable = false)
    private boolean backToStart;

    @Setter
    @Getter
    @Column(name = "distance_meters", nullable = false)
    private double distanceMeters;

    @Setter
    @Getter
    @Column(name = "elevation_gain", nullable = false)
    private double elevationGain;

    @Setter
    @Getter
    @Column(name = "elevation_loss", nullable = false)
    private double elevationLoss;

    @Setter
    @Getter
    @Column(name = "duration_seconds")
    private Integer durationSeconds;
}
