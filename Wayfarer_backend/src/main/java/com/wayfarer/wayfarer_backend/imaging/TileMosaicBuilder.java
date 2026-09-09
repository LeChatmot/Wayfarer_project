package com.wayfarer.wayfarer_backend.imaging;

import org.locationtech.jts.geom.Envelope;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

@Component
public class TileMosaicBuilder {

    private static final int TILE_SIZE = 256;

    private final TileCalculator tileCalculator;
    private final TileDownloader tileDownloader;

    public TileMosaicBuilder(TileCalculator tileCalculator, TileDownloader tileDownloader) {
        this.tileCalculator = tileCalculator;
        this.tileDownloader = tileDownloader;
    }

    public MosaicResult buildMosaic(String tileUrlTemplate, Envelope envelope, int zoom) {
        double minPixelX = tileCalculator.lonToPixelX(envelope.getMinX(), zoom);
        double maxPixelX = tileCalculator.lonToPixelX(envelope.getMaxX(), zoom);
        double minPixelY = tileCalculator.latToPixelY(envelope.getMaxY(), zoom);
        double maxPixelY = tileCalculator.latToPixelY(envelope.getMinY(), zoom);

        int startTileX = (int) Math.floor(minPixelX / TILE_SIZE);
        int endTileX = (int) Math.floor(maxPixelX / TILE_SIZE);
        int startTileY = (int) Math.floor(minPixelY / TILE_SIZE);
        int endTileY = (int) Math.floor(maxPixelY / TILE_SIZE);

        int mosaicWidth = (endTileX - startTileX + 1) * TILE_SIZE;
        int mosaicHeight = (endTileY - startTileY + 1) * TILE_SIZE;

        BufferedImage mosaic = new BufferedImage(mosaicWidth, mosaicHeight, BufferedImage.TYPE_INT_ARGB);
        var graphics = mosaic.createGraphics();

        for (int tileX = startTileX; tileX <= endTileX; tileX++) {
            for (int tileY = startTileY; tileY <= endTileY; tileY++) {
                BufferedImage tile = tileDownloader.downloadTile(tileUrlTemplate, new TileCoordinate(tileX, tileY, zoom));
                int drawX = (tileX - startTileX) * TILE_SIZE;
                int drawY = (tileY - startTileY) * TILE_SIZE;
                graphics.drawImage(tile, drawX, drawY, null);
            }
        }
        graphics.dispose();

        double mosaicOriginPixelX = startTileX * TILE_SIZE;
        double mosaicOriginPixelY = startTileY * TILE_SIZE;

        return new MosaicResult(mosaic, mosaicOriginPixelX, mosaicOriginPixelY, zoom);
    }

    public record MosaicResult(BufferedImage image, double originPixelX, double originPixelY, int zoom) {}
}