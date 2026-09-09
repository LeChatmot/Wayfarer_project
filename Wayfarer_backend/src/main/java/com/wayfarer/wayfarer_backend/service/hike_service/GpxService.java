package com.wayfarer.wayfarer_backend.service.hike_service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import org.locationtech.jts.geom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class GpxService {

    private final Logger log = LoggerFactory.getLogger(GpxService.class);
    private static final double ELEVATION_NOISE_THRESHOLD = 3.0;
    private static final double AVERAGE_SPEED_METER_PER_SECOND = 4.0 / 3.6;
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);
    private static final int IGN_BATCH_SIZE = 5000;
    private static final int MAX_TRACK_POINTS = 250_000;
    private static final long IGN_MIN_INTERVAL_MS = 200;
    private final Object ignRateLimitLock = new Object();
    private long ignNextAllowedRequestTimeMs = 0;

    private final ReverseGeocodeService reverseGeocodeService;
    private final RestClient ignClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GpxService(ReverseGeocodeService reverseGeocodeService) {
        this.reverseGeocodeService = reverseGeocodeService;
    }

    public ParsedGpx parse(String gpxContent) {
        List<WayPoint> points = extractTrackPoints(gpxContent);
        if (points.size() < 2) {
            throw new IllegalArgumentException("Le fichier GPX doit contenir au moins 2 points de trace");
        }
        if (points.size() > MAX_TRACK_POINTS) {
            throw new IllegalArgumentException(
                    "Le fichier GPX contient trop de points (" + points.size()
                            + "), maximum autorisé : " + MAX_TRACK_POINTS);
        }

        boolean hasElevation = points.stream().anyMatch(p -> p.getElevation().isPresent());
        List<double[]> coords = hasElevation
                ? toCoordsWithExistingElevation(points)
                : enrichWithIgnElevation(points);

        return computeMetrics(coords);
    }

    private List<WayPoint> extractTrackPoints(String gpxContent) {
        try {
            GPX gpx = GPX.Reader.of(GPX.Reader.Mode.LENIENT)
                    .read(new ByteArrayInputStream(gpxContent.getBytes()));
            return gpx.tracks()
                    .flatMap(Track::segments)
                    .flatMap(TrackSegment::points)
                    .toList();
        } catch (Exception e) {
            throw new IllegalArgumentException("Fichier GPX invalide", e);
        }
    }

    private List<double[]> toCoordsWithExistingElevation(List<WayPoint> points) {
        return points.stream()
                .map(p -> new double[]{
                        p.getLongitude().doubleValue(),
                        p.getLatitude().doubleValue(),
                        p.getElevation().map(Number::doubleValue).orElse(0.0)
                })
                .toList();
    }

    private List<double[]> enrichWithIgnElevation(List<WayPoint> points) {
        List<double[]> coords = new ArrayList<>();

        for (int start = 0; start < points.size(); start += IGN_BATCH_SIZE) {
            int end = Math.min(start + IGN_BATCH_SIZE, points.size());
            List<WayPoint> batch = points.subList(start, end);
            coords.addAll(fetchElevationBatch(batch));
        }

        return coords;
    }

    private List<double[]> fetchElevationBatch(List<WayPoint> batch) {
        String lons = buildCoordString(batch, true);
        String lats = buildCoordString(batch, false);
        List<double[]> batchCoords = new ArrayList<>();

        throttleIgnRequest();

        try {
            String response = ignClient.get()
                    .uri("https://data.geopf.fr/altimetrie/1.0/calcul/alti/rest/elevation.json?lon={lons}&lat={lats}&resource=ign_rge_alti_wld&delimiter=|&indent=false&measures=false&zonly=true",
                            lons, lats)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);
            JsonNode elevations = root.get("elevations");

            for (int i = 0; i < batch.size(); i++) {
                double z = (elevations != null && i < elevations.size())
                        ? elevations.get(i).asDouble()
                        : 0.0;
                if (z <= -99999.0) {
                    z = 0.0;
                }
                batchCoords.add(new double[]{
                        batch.get(i).getLongitude().doubleValue(),
                        batch.get(i).getLatitude().doubleValue(),
                        z
                });
            }
        } catch (Exception e) {
            log.error("Échec de la récupération des élévations IGN pour un batch de {} points", batch.size(), e);
            for (WayPoint p : batch) {
                batchCoords.add(new double[]{
                        p.getLongitude().doubleValue(),
                        p.getLatitude().doubleValue(),
                        0.0
                });
            }
        }

        return batchCoords;
    }

    private String buildCoordString(List<WayPoint> points, boolean longitude) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < points.size(); i++) {
            if (i > 0) sb.append('|');
            WayPoint p = points.get(i);
            sb.append(longitude ? p.getLongitude().doubleValue() : p.getLatitude().doubleValue());
        }
        return sb.toString();
    }

    private ParsedGpx computeMetrics(List<double[]> coords) {
        Coordinate[] jtsCoords = coords.stream()
                .map(c -> new Coordinate(c[0], c[1], c[2]))
                .toArray(Coordinate[]::new);

        LineString path = GEOMETRY_FACTORY.createLineString(jtsCoords);
        Point startingPoint = GEOMETRY_FACTORY.createPoint(jtsCoords[0]);

        String startingPointName = reverseGeocodeService.getPlaceNameFromCoordinates(
                jtsCoords[0].getY(),
                jtsCoords[0].getX()
        );

        double distance = 0;
        double gain = 0;
        double loss = 0;

        for (int i = 1; i < coords.size(); i++) {
            distance += haversine(coords.get(i - 1), coords.get(i));
            double dz = coords.get(i)[2] - coords.get(i - 1)[2];
            if (Math.abs(dz) >= ELEVATION_NOISE_THRESHOLD) {
                if (dz > 0) gain += dz;
                else loss += Math.abs(dz);
            }
        }

        int duration = (int) (distance / AVERAGE_SPEED_METER_PER_SECOND);
        return new ParsedGpx(path, startingPoint, startingPointName, distance, gain, loss, duration);
    }

    private double haversine(double[] a, double[] b) {
        double r = 6371000;
        double dLat = Math.toRadians(b[1] - a[1]);
        double dLon = Math.toRadians(b[0] - a[0]);
        double h = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(a[1])) * Math.cos(Math.toRadians(b[1]))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * r * Math.asin(Math.sqrt(h));
    }

    private void throttleIgnRequest() {
        synchronized (ignRateLimitLock) {
            long now = System.currentTimeMillis();
            long waitMs = ignNextAllowedRequestTimeMs - now;
            if (waitMs > 0) {
                try {
                    Thread.sleep(waitMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrompu pendant l'attente du rate limit IGN", e);
                }
                now = System.currentTimeMillis();
            }
            ignNextAllowedRequestTimeMs = Math.max(now, ignNextAllowedRequestTimeMs) + IGN_MIN_INTERVAL_MS;
        }
    }
}