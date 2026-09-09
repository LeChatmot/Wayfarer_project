package com.wayfarer.wayfarer_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import java.time.Instant;

@Setter
@Getter
@Entity
@Table(name = "hikes")
public class Hike extends BaseEntity {

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(nullable = false, length = 60)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_creator")
    private User creator;

    @Column(name = "starting_point", nullable = false, columnDefinition = "geography(POINTZ,4326)")
    private Point startingPoint;

    @Column(name = "starting_point_name", nullable = false)
    private String startingPointName;

    @Column(nullable = false, columnDefinition = "geography(LineStringZ,4326)")
    private LineString path;

    @Column(name = "back_to_start", nullable = false)
    private boolean backToStart;

    @Column(name = "distance_meters", nullable = false)
    private double distanceMeters;

    @Column(name = "elevation_gain", nullable = false)
    private double elevationGain;

    @Column(name = "elevation_loss", nullable = false)
    private double elevationLoss;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty")
    private HikeDifficulty hikeDifficulty;

    @Column(name = "preview_image_light_url", length = 500)
    private String previewImageLightUrl;

    @Column(name = "preview_image_dark_url", length = 500)
    private String previewImageDarkUrl;

}
