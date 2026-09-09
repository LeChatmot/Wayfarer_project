package com.wayfarer.wayfarer_backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/static/hike-previews")
public class HikePreviewImageController {

    @Value("${wayfarer.storage.hike-previews-path}")
    private String storagePath;

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> getPreviewImage(@PathVariable String fileName) {
        Path filePath = Paths.get(storagePath, fileName).normalize();
        File file = filePath.toFile();

        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.notFound().build();
        }

        if (!filePath.startsWith(Paths.get(storagePath).normalize())) {
            return ResponseEntity.badRequest().build();
        }

        Resource resource = new FileSystemResource(file);

        try {
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic())
                    .body(resource);
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().build();
        }
    }
}