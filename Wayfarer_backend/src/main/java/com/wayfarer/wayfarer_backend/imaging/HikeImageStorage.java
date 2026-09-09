package com.wayfarer.wayfarer_backend.imaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class HikeImageStorage {

    @Value("${wayfarer.storage.hike-previews-path}")
    private String storagePath;

    @Value("${wayfarer.storage.hike-previews-public-url}")
    private String publicUrlPrefix;

    public String save(BufferedImage image, Integer hikeId, String variant) {
        String fileName = "hike-" + hikeId + "-" + variant + "-" + UUID.randomUUID() + ".png";
        File targetFile = new File(storagePath, fileName);

        try {
            File parentDir = targetFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            ImageIO.write(image, "png", targetFile);
        } catch (IOException exception) {
            throw new RuntimeException("Impossible d'enregistrer l'image de preview", exception);
        }

        return publicUrlPrefix + "/" + fileName;
    }
}