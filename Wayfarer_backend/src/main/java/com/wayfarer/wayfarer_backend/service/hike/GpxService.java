package com.wayfarer.wayfarer_backend.service.hike;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.WayPoint;
import org.locationtech.jts.geom.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class GpxService {

    private static final double ELEVATION_NOISE_THRESHOLD = 3.0;
    private static final double AVERAGE_SPEED_METER_PER_SECOND = 4.0 / 3.6;
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    private final RestClient ignClient = RestClient.create();

    public ParsedGpx parse(String gpxContent) {
        List<WayPoint> points = extractTrackPoints(gpxContent);
        if (points.size() < 2) {
            throw new IllegalArgumentException("Le fichier GPX doit contenir au moins 2 points de trace");
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
                    .flatMap(t -> t.segments())
                    .flatMap(s -> s.points())
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
                        p.getElevation().map(e -> e.doubleValue()).orElse(0.0)
                })
                .toList();
    }

    private List<double[]> enrichWithIgnElevation(List<WayPoint> points) {
        List<double[]> coords = new ArrayList<>();
        String lons = buildCoordString(points, true);
        String lats = buildCoordString(points, false);

        try {
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(ignClient.get()
                            .uri("https://data.geopf.fr/altimetrie/1.0/calcul/alti/rest/elevation.json?lon={lons}&lat={lats}&resource=ign_rge_alti_5&delimiter=|&indent=false&measures=false&zonly=true",
                                    lons, lats)
                            .retrieve()
                            .body(java.io.InputStream.class));

            NodeList elevations = doc.getElementsByTagName("elevation");
            for (int i = 0; i < points.size(); i++) {
                double z = i < elevations.getLength()
                        ? Double.parseDouble(elevations.item(i).getTextContent())
                        : 0.0;
                coords.add(new double[]{
                        points.get(i).getLongitude().doubleValue(),
                        points.get(i).getLatitude().doubleValue(),
                        z
                });
            }
        } catch (Exception e) {
            for (WayPoint p : points) {
                coords.add(new double[]{
                        p.getLongitude().doubleValue(),
                        p.getLatitude().doubleValue(),
                        0.0
                });
            }
        }
        return coords;
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
        return new ParsedGpx(path, startingPoint, distance, gain, loss, duration);
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

    public record ParsedGpx(
            LineString path,
            Point startingPoint,
            double distanceMeters,
            double elevationGain,
            double elevationLoss,
            int durationSeconds
    ) {}
}