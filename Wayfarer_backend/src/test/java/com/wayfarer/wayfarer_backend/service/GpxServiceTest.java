package com.wayfarer.wayfarer_backend.service;

import com.wayfarer.wayfarer_backend.service.hike_service.GpxService;
import com.wayfarer.wayfarer_backend.service.hike_service.ParsedGpx;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GpxServiceTest {

    private GpxService gpxService;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(gpxService, "ignClient", restClient);
    }

    private String gpxWithElevation() {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <gpx version="1.1" creator="test" xmlns="http://www.topografix.com/GPX/1/1">
                    <trk>
                        <trkseg>
                            <trkpt lat="45.0000" lon="6.0000"><ele>1000</ele></trkpt>
                            <trkpt lat="45.0010" lon="6.0010"><ele>1010</ele></trkpt>
                            <trkpt lat="45.0020" lon="6.0020"><ele>995</ele></trkpt>
                        </trkseg>
                    </trk>
                </gpx>
                """;
    }

    private String gpxWithoutElevation() {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <gpx version="1.1" creator="test" xmlns="http://www.topografix.com/GPX/1/1">
                    <trk>
                        <trkseg>
                            <trkpt lat="45.0000" lon="6.0000"></trkpt>
                            <trkpt lat="45.0010" lon="6.0010"></trkpt>
                        </trkseg>
                    </trk>
                </gpx>
                """;
    }

    private String gpxWithSinglePoint() {
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <gpx version="1.1" creator="test" xmlns="http://www.topografix.com/GPX/1/1">
                    <trk>
                        <trkseg>
                            <trkpt lat="45.0000" lon="6.0000"><ele>1000</ele></trkpt>
                        </trkseg>
                    </trk>
                </gpx>
                """;
    }

    @Test
    @DisplayName("Doit calculer les métriques directement quand l'élévation est présente dans le GPX")
    void shouldComputeMetricsWithExistingElevation() {
        ParsedGpx result = gpxService.parse(gpxWithElevation());

        assertThat(result.distanceMeters()).isGreaterThan(0);
        assertThat(result.elevationGain()).isGreaterThanOrEqualTo(0);
        assertThat(result.elevationLoss()).isGreaterThanOrEqualTo(0);
        assertThat(result.durationSeconds()).isGreaterThan(0);
        assertThat(result.path().getNumPoints()).isEqualTo(3);
        assertThat(result.startingPoint().getX()).isEqualTo(6.0000);
        assertThat(result.startingPoint().getY()).isEqualTo(45.0000);

        verifyNoInteractions(restClient);
    }

    @Test
    @DisplayName("Doit lever une exception si le GPX contient moins de 2 points")
    void shouldThrowExceptionWhenLessThanTwoPoints() {
        assertThatThrownBy(() -> gpxService.parse(gpxWithSinglePoint()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("au moins 2 points");
    }

    @Test
    @DisplayName("Doit lever une exception si le fichier GPX est invalide")
    void shouldThrowExceptionWhenGpxIsInvalid() {
        String invalidGpx = "ceci n'est pas un fichier gpx valide";

        assertThatThrownBy(() -> gpxService.parse(invalidGpx))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Fichier GPX invalide");
    }

    @Test
    @DisplayName("Doit appeler l'API IGN et enrichir les points quand l'élévation est absente")
    void shouldCallIgnApiWhenElevationIsMissing() {
        String ignResponseXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <root>
                    <elevation>1000.0</elevation>
                    <elevation>1050.0</elevation>
                </root>
                """;
        InputStream ignResponseStream = new ByteArrayInputStream(
                ignResponseXml.getBytes(StandardCharsets.UTF_8));

        mockIgnCallReturning(ignResponseStream);

        ParsedGpx result = gpxService.parse(gpxWithoutElevation());

        assertThat(result.distanceMeters()).isGreaterThan(0);
        assertThat(result.elevationGain()).isGreaterThan(0);
        assertThat(result.durationSeconds()).isGreaterThan(0);

        verify(restClient).get();
    }

    @Test
    @DisplayName("Doit retourner une élévation à zéro si l'appel IGN échoue")
    void shouldFallbackToZeroElevationWhenIgnCallFails() {
        //Arrange
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenThrow(new RuntimeException("IGN indisponible"));

        //Act
        ParsedGpx result = gpxService.parse(gpxWithoutElevation());

        //Assert
        assertThat(result.elevationGain()).isZero();
        assertThat(result.elevationLoss()).isZero();
        assertThat(result.distanceMeters()).isGreaterThan(0);
    }

    @SuppressWarnings("unchecked")
    private void mockIgnCallReturning(InputStream responseStream) {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(InputStream.class)).thenReturn(responseStream);
    }
}