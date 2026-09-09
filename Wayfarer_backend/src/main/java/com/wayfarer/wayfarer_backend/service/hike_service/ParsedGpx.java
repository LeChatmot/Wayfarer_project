package com.wayfarer.wayfarer_backend.service.hike_service;

import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

public record ParsedGpx(
        LineString path,
        Point startingPoint,
        String startingPointName,
        double distanceMeters,
        double elevationGain,
        double elevationLoss,
        int durationSeconds
) {}