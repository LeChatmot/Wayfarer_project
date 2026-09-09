package com.wayfarer.wayfarer_backend.imaging;

import org.locationtech.jts.geom.Envelope;
import org.springframework.stereotype.Component;

@Component
public class TileCalculator {

    private static final int TILE_SIZE = 256;

    public int computeZoomForEnvelope(Envelope envelope, int imageWidth, int imageHeight) {
        for (int zoom = 18; zoom >= 0; zoom--) {
            double minXPixel = lonToPixelX(envelope.getMinX(), zoom);
            double maxXPixel = lonToPixelX(envelope.getMaxX(), zoom);
            double minYPixel = latToPixelY(envelope.getMaxY(), zoom);
            double maxYPixel = latToPixelY(envelope.getMinY(), zoom);

            double pixelWidth = maxXPixel - minXPixel;
            double pixelHeight = maxYPixel - minYPixel;

            if (pixelWidth <= imageWidth && pixelHeight <= imageHeight) {
                return zoom;
            }
        }
        return 0;
    }

    public double lonToPixelX(double longitude, int zoom) {
        return (longitude + 180.0) / 360.0 * TILE_SIZE * Math.pow(2, zoom);
    }

    public double latToPixelY(double latitude, int zoom) {
        double latRad = Math.toRadians(latitude);
        return (1 - Math.log(Math.tan(latRad) + 1 / Math.cos(latRad)) / Math.PI) / 2.0 * TILE_SIZE * Math.pow(2, zoom);
    }

    public TileCoordinate pixelToTile(double pixelX, double pixelY, int zoom) {
        return new TileCoordinate((int) (pixelX / TILE_SIZE), (int) (pixelY / TILE_SIZE), zoom);
    }
}