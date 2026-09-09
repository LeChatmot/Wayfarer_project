package com.wayfarer.wayfarer_backend.service.hike_service;

import com.google.gson.internal.Streams;
import com.wayfarer.wayfarer_backend.imaging.HikePreviewImageGenerator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class HikeHydrationService {

    GpxService gpxService;
    HikePreviewImageGenerator previewImageGenerator;

    @Async
    public void hydrateAsync(Integer hikeId, String gpxContent) {
        try {
            ParsedGpx parsed = gpxService.parse(gpxContent);
            previewImageGenerator.generateAndAttach(hikeId);
        } catch (Exception e) {
        }
    }
}