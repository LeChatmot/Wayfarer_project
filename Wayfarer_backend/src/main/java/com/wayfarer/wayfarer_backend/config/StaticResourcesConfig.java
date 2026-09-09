package com.wayfarer.wayfarer_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class StaticResourcesConfig implements WebMvcConfigurer {

    @Value("${wayfarer.storage.hike-previews-path}")
    private String hikePreviewsPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        File previewsDir = new File(hikePreviewsPath);
        if (!previewsDir.exists()) {
            previewsDir.mkdirs();
        }

        registry.addResourceHandler("/static/hike-previews/**")
                .addResourceLocations("file:" + hikePreviewsPath + "/")
                .setCachePeriod(31536000)
                .resourceChain(true)
                .addResolver(new org.springframework.web.servlet.resource.VersionResourceResolver()
                        .addFixedVersionStrategy("1.0.0", "/**"));
    }
}