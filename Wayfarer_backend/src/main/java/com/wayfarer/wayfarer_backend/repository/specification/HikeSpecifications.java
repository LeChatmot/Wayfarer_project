package com.wayfarer.wayfarer_backend.repository.specification;

import com.wayfarer.wayfarer_backend.dto.hike_dto.HikeSearchCriteria;
import com.wayfarer.wayfarer_backend.model.Hike;
import jakarta.persistence.criteria.Predicate;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class HikeSpecifications {

    private HikeSpecifications() {
        /* This utility class should not be instantiated */
    }

    private static final double DEFAULT_RADIUS_METERS = 50000;
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    public static Specification<Hike> withCriteria(HikeSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();


            if (criteria.latitude() != null && criteria.longitude() != null){
                Point searchPoint = GEOMETRY_FACTORY.createPoint(
                        new Coordinate(criteria.longitude(), criteria.latitude())
                );

                double radius = criteria.radiusMeters() != null ? criteria.radiusMeters() : DEFAULT_RADIUS_METERS;

                predicates.add(cb.isTrue(
                        cb.function(
                                "st_dwithin",
                                Boolean.class,
                                root.get("startingPoint"),
                                cb.literal(searchPoint),
                                cb.literal(radius),
                                cb.literal(true)
                        )
                ));
            }

            if (criteria.difficulty() != null) {
                predicates.add(cb.equal(root.get("hikeDifficulty"), criteria.difficulty()));
            }

            if (criteria.backToStart() != null) {
                predicates.add(cb.equal(root.get("backToStart"), criteria.backToStart()));
            }

            if (criteria.minDistanceMeters() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("distanceMeters"), criteria.minDistanceMeters()));
            }
            if (criteria.maxDistanceMeters() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("distanceMeters"), criteria.maxDistanceMeters()));
            }

            if (criteria.minElevationGain() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("elevationGain"), criteria.minElevationGain()));
            }
            if (criteria.maxElevationGain() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("elevationGain"), criteria.maxElevationGain()));
            }

            if (criteria.minElevationLoss() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("elevationLoss"), criteria.minElevationLoss()));
            }
            if (criteria.maxElevationLoss() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("elevationLoss"), criteria.maxElevationLoss()));
            }

            if (criteria.minDurationSeconds() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("durationSeconds"), criteria.minDurationSeconds()));
            }
            if (criteria.maxDurationSeconds() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("durationSeconds"), criteria.maxDurationSeconds()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}