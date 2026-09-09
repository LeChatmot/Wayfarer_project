package com.wayfarer.wayfarer_backend.imaging;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.LineString;
import org.springframework.stereotype.Component;

@Component
public class HikeEnvelopeCalculator {

    public Envelope computePaddedEnvelope(LineString path, int imageWidth, int imageHeight, int paddingPixels, TileCalculator tileCalculator) {
        Envelope raw = path.getEnvelopeInternal();

        int zoom = tileCalculator.computeZoomForEnvelope(raw, imageWidth - 2 * paddingPixels, imageHeight - 2 * paddingPixels);

        double paddingLon = paddingPixels / (tileCalculator.lonToPixelX(raw.getMaxX(), zoom) - tileCalculator.lonToPixelX(raw.getMinX(), zoom))
                * (raw.getMaxX() - raw.getMinX());
        double paddingLat = paddingPixels / (tileCalculator.latToPixelY(raw.getMinY(), zoom) - tileCalculator.latToPixelY(raw.getMaxY(), zoom))
                * (raw.getMaxY() - raw.getMinY());

        return new Envelope(
                raw.getMinX() - paddingLon,
                raw.getMaxX() + paddingLon,
                raw.getMinY() - paddingLat,
                raw.getMaxY() + paddingLat
        );
    }
}