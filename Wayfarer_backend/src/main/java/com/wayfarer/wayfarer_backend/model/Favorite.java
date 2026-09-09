package com.wayfarer.wayfarer_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Getter
@Table(name = "favorites")
@IdClass(Favorite.FavoriteId.class)
public class Favorite {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hike_id", nullable = false)
    private Hike hike;

    @Column(
            name = "created_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private Instant createdAt;

    protected Favorite() {
    }

    public Favorite(User user, Hike hike) {
        this.user = Objects.requireNonNull(user);
        this.hike = Objects.requireNonNull(hike);
    }

    public static class FavoriteId implements Serializable {

        private Integer user;
        private Integer hike;

        public FavoriteId() {
        }

        public FavoriteId(Integer user, Integer hike) {
            this.user = user;
            this.hike = hike;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }

            if (!(object instanceof FavoriteId favoriteId)) {
                return false;
            }

            return Objects.equals(user, favoriteId.user)
                    && Objects.equals(hike, favoriteId.hike);
        }

        @Override
        public int hashCode() {
            return Objects.hash(user, hike);
        }
    }
}