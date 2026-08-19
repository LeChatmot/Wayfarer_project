package com.wayfarer.wayfarer_backend.model;

import jakarta.persistence.*;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Entity
@Table(name = "hikes")
public class Hike extends BaseEntity{

    @Column(nullable = false, length = 60)
    private String name;

    @Column(name = "id_creator", nullable = false)
    private Long creatorId;

    @Column(nullable = false, columnDefinition = "geometry(Point,4326)")
    private Point startingPoint;  // Champ géométrique PostGIS

    @Column(name = "back_to_start", nullable = false)
    private boolean backToStart = false;

    @Column(name = "id_document_mongo")
    private Long mongoDocumentId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Point getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(Point startingPoint) {
        this.startingPoint = startingPoint;
    }

    public boolean isBackToStart() {
        return backToStart;
    }

    public void setBackToStart(boolean backToStart) {
        this.backToStart = backToStart;
    }

    public Long getMongoDocumentId() {
        return mongoDocumentId;
    }

    public void setMongoDocumentId(Long mongoDocumentId) {
        this.mongoDocumentId = mongoDocumentId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
