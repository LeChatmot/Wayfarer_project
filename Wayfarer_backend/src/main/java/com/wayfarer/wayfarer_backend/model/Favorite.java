package com.wayfarer.wayfarer_backend.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "favorites")
@IdClass(Favorite.FavoriteId.class)
public class Favorite {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hike_id")
    @Getter
    private Hike hike;

    public Favorite() {}

    public Favorite(User user, Hike hike) {
        this.user = user;
        this.hike = hike;
    }

    public static class FavoriteId implements Serializable {
        private Long user;
        private Long hike;

        public FavoriteId() {}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FavoriteId that)) return false;
            return Objects.equals(user, that.user) && Objects.equals(hike, that.hike);
        }

        @Override
        public int hashCode() {
            return Objects.hash(user, hike);
        }
    }
}