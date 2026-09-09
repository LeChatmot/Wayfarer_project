package com.wayfarer.wayfarer_backend.service.hike_service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ReverseGeocodeService {

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getPlaceNameFromCoordinates(double lat, double lon) {
        try {
            String response = restClient.get()
                    .uri("https://nominatim.openstreetmap.org/reverse?format=json&lat={lat}&lon={lon}&zoom=10&addressdetails=1",
                            lat, lon)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);
            String addressFieldName = "address";
            return root.path(addressFieldName)
                    .path("city")
                    .asText(root.path(addressFieldName).path("town").asText(
                            root.path(addressFieldName).path("village").asText("Lieu inconnu")));
        } catch (Exception exception) {
            return "Lieu inconnu";
        }
    }
}