package com.wayfarer.wayfarer_backend.imaging;

import com.wayfarer.wayfarer_backend.model.Hike;
import com.wayfarer.wayfarer_backend.model.MapProvider;
import com.wayfarer.wayfarer_backend.repository.HikeRepository;
import com.wayfarer.wayfarer_backend.service.mapProvider_service.MapProviderService;
import org.locationtech.jts.geom.Envelope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

@Service
public class HikePreviewImageGenerator {

    private static final int IMAGE_WIDTH = 250;
    private static final int IMAGE_HEIGHT = 300;
    private static final int PADDING_PIXELS = 5;
    private static final double DARK_INVERSION_INTENSITY = 0.8;

    private final MapProviderService mapProviderService;
    private final TileCalculator tileCalculator;
    private final HikeEnvelopeCalculator envelopeCalculator;
    private final TileMosaicBuilder mosaicBuilder;
    private final HikeTraceRenderer traceRenderer;
    private final ColorInversionFilter colorInversionFilter;
    private final HikeImageStorage imageStorage;
    private final HikeRepository hikeRepository;

    public HikePreviewImageGenerator(MapProviderService mapProviderService,
                                     TileCalculator tileCalculator,
                                     HikeEnvelopeCalculator envelopeCalculator,
                                     TileMosaicBuilder mosaicBuilder,
                                     HikeTraceRenderer traceRenderer,
                                     ColorInversionFilter colorInversionFilter,
                                     HikeImageStorage imageStorage,
                                     HikeRepository hikeRepository) {
        this.mapProviderService = mapProviderService;
        this.tileCalculator = tileCalculator;
        this.envelopeCalculator = envelopeCalculator;
        this.mosaicBuilder = mosaicBuilder;
        this.traceRenderer = traceRenderer;
        this.colorInversionFilter = colorInversionFilter;
        this.imageStorage = imageStorage;
        this.hikeRepository = hikeRepository;
    }

    @Async("hikeImageExecutor")
    public void generateAndAttach(Integer hikeId) {
        Hike hike = hikeRepository.findById(hikeId)
                .orElseThrow(() -> new IllegalArgumentException("Randonnée introuvable pour la génération d'image"));

        MapProvider provider = mapProviderService.getMapProviderPlanOpenStreet();

        Envelope envelope = envelopeCalculator.computePaddedEnvelope(
                hike.getPath(), IMAGE_WIDTH, IMAGE_HEIGHT, PADDING_PIXELS, tileCalculator);

        int zoom = tileCalculator.computeZoomForEnvelope(envelope, IMAGE_WIDTH - 2 * PADDING_PIXELS, IMAGE_HEIGHT - 2 * PADDING_PIXELS);

        TileMosaicBuilder.MosaicResult mosaic = mosaicBuilder.buildMosaic(provider.getUrl(), envelope, zoom);

        BufferedImage lightImage = traceRenderer.renderCroppedWithTrace(mosaic, hike.getPath(), IMAGE_WIDTH, IMAGE_HEIGHT, PADDING_PIXELS);
        BufferedImage darkImage = colorInversionFilter.applyPartialInversion(lightImage, DARK_INVERSION_INTENSITY);

        String lightUrl = imageStorage.save(lightImage, hikeId, "light");
        String darkUrl = imageStorage.save(darkImage, hikeId, "dark");

        hike.setPreviewImageLightUrl(lightUrl);
        hike.setPreviewImageDarkUrl(darkUrl);
        hikeRepository.save(hike);
    }
}