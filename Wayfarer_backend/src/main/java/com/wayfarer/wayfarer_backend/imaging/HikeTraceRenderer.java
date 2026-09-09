package com.wayfarer.wayfarer_backend.imaging;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.LineString;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Path2D;

@Component
public class HikeTraceRenderer {

    private final TileCalculator tileCalculator;

    public HikeTraceRenderer(TileCalculator tileCalculator) {
        this.tileCalculator = tileCalculator;
    }

    public BufferedImage renderCroppedWithTrace(TileMosaicBuilder.MosaicResult mosaic, LineString path,
                                                int targetWidth, int targetHeight, int paddingPixels) {
        Coordinate[] coordinates = path.getCoordinates();

        double minPixelX = Double.MAX_VALUE;
        double maxPixelX = -Double.MAX_VALUE;
        double minPixelY = Double.MAX_VALUE;
        double maxPixelY = -Double.MAX_VALUE;

        double[] pathPixelX = new double[coordinates.length];
        double[] pathPixelY = new double[coordinates.length];

        for (int i = 0; i < coordinates.length; i++) {
            double px = tileCalculator.lonToPixelX(coordinates[i].x, mosaic.zoom()) - mosaic.originPixelX();
            double py = tileCalculator.latToPixelY(coordinates[i].y, mosaic.zoom()) - mosaic.originPixelY();
            pathPixelX[i] = px;
            pathPixelY[i] = py;
            minPixelX = Math.min(minPixelX, px);
            maxPixelX = Math.max(maxPixelX, px);
            minPixelY = Math.min(minPixelY, py);
            maxPixelY = Math.max(maxPixelY, py);
        }

        double pathWidth = maxPixelX - minPixelX;
        double pathHeight = maxPixelY - minPixelY;

        double availableWidthForPath = targetWidth - 2 * paddingPixels;
        double availableHeightForPath = targetHeight - 2 * paddingPixels;

        double offsetX = (availableWidthForPath - pathWidth) / 2.0;
        double offsetY = (availableHeightForPath - pathHeight) / 2.0;

        double cropX = minPixelX - paddingPixels - offsetX;
        double cropY = minPixelY - paddingPixels - offsetY;

        BufferedImage result = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = result.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, targetWidth, targetHeight);

        graphics.drawImage(mosaic.image(), (int) -cropX, (int) -cropY, null);

        Path2D tracePath = new Path2D.Double();
        tracePath.moveTo(pathPixelX[0] - cropX, pathPixelY[0] - cropY);
        for (int i = 1; i < coordinates.length; i++) {
            tracePath.lineTo(pathPixelX[i] - cropX, pathPixelY[i] - cropY);
        }

        graphics.setColor(new Color(230, 60, 60));
        graphics.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.draw(tracePath);

        graphics.dispose();
        return result;
    }
}