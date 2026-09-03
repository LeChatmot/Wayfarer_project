package com.wayfarer.wayfarer_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "hikes")
public class Hike extends BaseEntity {

    @Column(nullable = false, length = 60)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_creator", nullable = false)
    private User creator;

    @Column(name = "starting_point", nullable = false, columnDefinition = "geometry(POINTZ,4326)")
    private Point startingPoint;

    @Column(nullable = false, columnDefinition = "geometry(LineStringZ,4326)")
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

    public void setDifficultyFromKmEffort(){
        Double kmEffort = this.getDistanceMeters()/1000 + this.getElevationGain()/100 + this.getElevationLoss()/400;
        if (kmEffort < 10L){
            this.setHikeDifficulty(HikeDifficulty.EASY);
        } else if (kmEffort < 20L){
            this.setHikeDifficulty(HikeDifficulty.MEDIUM);
        }  else if (kmEffort < 30L){
            this.setHikeDifficulty(HikeDifficulty.HARD);
        }
        this.setHikeDifficulty(HikeDifficulty.VERY_HARD);
    }
}
