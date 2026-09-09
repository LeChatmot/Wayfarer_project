package com.wayfarer.wayfarer_backend.imaging;

import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

@Component
public class ColorInversionFilter {

    public BufferedImage applyPartialInversion(BufferedImage source, double intensity) {
        int width = source.getWidth();
        int height = source.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int argb = source.getRGB(x, y);
                int alpha = (argb >> 24) & 0xFF;
                int red = mixWithInverse((argb >> 16) & 0xFF, intensity);
                int green = mixWithInverse((argb >> 8) & 0xFF, intensity);
                int blue = mixWithInverse(argb & 0xFF, intensity);
                result.setRGB(x, y, (alpha << 24) | (red << 16) | (green << 8) | blue);
            }
        }

        return result;
    }

    private int mixWithInverse(int channelValue, double intensity) {
        double inverted = 255 - channelValue;
        double mixed = channelValue * (1 - intensity) + inverted * intensity;
        return (int) Math.round(mixed);
    }
}