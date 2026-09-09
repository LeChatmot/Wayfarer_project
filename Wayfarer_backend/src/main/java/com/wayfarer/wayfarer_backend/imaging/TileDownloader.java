package com.wayfarer.wayfarer_backend.imaging;

import com.wayfarer.wayfarer_backend.exception.TileDownloadException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

@Component
public class TileDownloader {

    private final RestClient restClient;

    public TileDownloader() {
        this.restClient = RestClient.create();
    }

    public BufferedImage downloadTile(String urlTemplate, TileCoordinate tile) {
        String url = urlTemplate
                .replace("{z}", String.valueOf(tile.zoom()))
                .replace("{y}", String.valueOf(tile.y()))
                .replace("{x}", String.valueOf(tile.x()));

        byte[] tileBytes = restClient.get()
                .uri(url)
                .retrieve()
                .body(byte[].class);

        try {
            return ImageIO.read(new ByteArrayInputStream(tileBytes));
        } catch (Exception exception) {
            throw new TileDownloadException("Impossible de lire la tuile à l'URL " + url, exception);
        }
    }
}