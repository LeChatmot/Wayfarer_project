package com.wayfarer.wayfarer_backend.repository;

import com.wayfarer.wayfarer_backend.model.Hike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HikeRepository extends JpaRepository<Hike, Integer> {

    @Query(value = """
            SELECT * FROM hikes
            WHERE ST_DWithin(starting_point::geography, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography, :radiusMeters)
            ORDER BY ST_Distance(starting_point::geography, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography)
            """, nativeQuery = true)
    List<Hike> findNearStartingPoint(@Param("lat") double lat,
                                     @Param("lon") double lon,
                                     @Param("radiusMeters") double radiusMeters);
}