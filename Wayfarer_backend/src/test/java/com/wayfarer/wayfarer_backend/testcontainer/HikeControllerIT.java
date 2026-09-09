package com.wayfarer.wayfarer_backend.testcontainer;

import com.wayfarer.wayfarer_backend.dto.hike_dto.HikeCreateRequest;
import com.wayfarer.wayfarer_backend.dto.hike_dto.HikeUpdateRequest;
import com.wayfarer.wayfarer_backend.imaging.HikePreviewImageGenerator;
import com.wayfarer.wayfarer_backend.repository.FavoriteRepository;
import com.wayfarer.wayfarer_backend.repository.HikeRepository;
import com.wayfarer.wayfarer_backend.repository.UserRepository;
import com.wayfarer.wayfarer_backend.service.auth_service.JwtService;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class HikeControllerIT {

    @LocalServerPort
    private int port;

    @MockBean
    private HikePreviewImageGenerator previewImageGenerator;

    @Autowired
    private HikeRepository hikeRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private String tokenUserA;
    private String tokenUserB;

    static DockerImageName postgisImage = DockerImageName.parse("postgis/postgis:15-3.3")
            .asCompatibleSubstituteFor("postgres");

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer(postgisImage)
            .withDatabaseName("wayfarer")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
        registry.add("spring.flyway.locations", () -> "classpath:db/migration");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("jwt.secret", () -> "wayfarer-test-secret-key-with-at-least-32-chars");
        registry.add("app.security.encryption-key", () -> "1LVtuU0LtDsGmflaxXUsFrpwUfktfne8YXpQ+AWhaiE=");
        registry.add("app.security.hmac-key", () -> "1LVtuU0LtDsGmflaxXUsFrpwUfktfne8YXpQ+AWhaiE=");
        registry.add("app.cors.allowed-origins", () -> "http://localhost:4200");
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.basePath = "/api/hikes";
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Origin", "http://localhost:4200")
                .build();

        favoriteRepository.deleteAll();
        hikeRepository.deleteAll();
        userRepository.deleteAll();

        var userA = userRepository.save(
                TestUserFactory.build("Bi4cadWU2XHmjK6cAY+lC/Ce5xGdSt7tgt+XyDepJCw=", "userA"));
        var userB = userRepository.save(
                TestUserFactory.build("9HphquG6Z9oWSYjTGqLHzGTjBkO+WpnKa3Q2gIwzrfU=", "userB"));

        tokenUserA = jwtService.generateAccessToken(userA.getId());
        tokenUserB = jwtService.generateAccessToken(userB.getId());
    }

    @AfterEach
    void tearDown() {
        favoriteRepository.deleteAll();
        hikeRepository.deleteAll();
        userRepository.deleteAll();
        RestAssured.reset();
    }

    private String loadGpx(String fileName) {
        try (InputStream in = getClass().getResourceAsStream("/gpx/" + fileName)) {
            if (in == null) {
                throw new IllegalStateException("Fichier GPX de test introuvable : " + fileName);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Lecture du GPX de test impossible", e);
        }
    }

    private HikeCreateRequest easyHikeRequest() {
        return new HikeCreateRequest(
                "Boucle du lac Pavin",
                "Petite boucle familiale autour du lac",
                true,
                loadGpx("circuit-des-villages-d-avenir.gpx"));
    }

    private HikeCreateRequest mediumHikeRequest() {
        return new HikeCreateRequest(
                "Randonnée du canyon des gueulards",
                "Belle rando avec vue panoramique",
                false,
                loadGpx("canyon-des-gueulards-via-le-chaffal.gpx"));
    }

    private Integer createHikeAs(String token, HikeCreateRequest request) {
        return given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("id");
    }

    private Integer createHikeAs(String token) {
        return createHikeAs(token, easyHikeRequest());
    }

    @Test
    @DisplayName("Un utilisateur authentifié peut créer une randonnée à partir d'un GPX")
    void shouldCreateHikeWhenAuthenticated() {
        HikeCreateRequest request = mediumHikeRequest();

        given()
                .header("Authorization", "Bearer " + tokenUserA)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", notNullValue())
                .body("name", equalTo(request.name()))
                .body("description", equalTo(request.description()))
                .body("difficulty", notNullValue())
                .body("distanceMeters", greaterThan(0f))
                .body("elevationGain", greaterThanOrEqualTo(0f));
    }

    @Test
    @DisplayName("La création est refusée sans jeton d'authentification")
    void shouldRejectCreateWhenNotAuthenticated() {
        given()
                .contentType(ContentType.JSON)
                .body(easyHikeRequest())
                .when()
                .post("")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("Le créateur peut modifier sa propre randonnée")
    void shouldUpdateOwnHike() {
        Integer hikeId = createHikeAs(tokenUserA);

        given()
                .header("Authorization", "Bearer " + tokenUserA)
                .contentType(ContentType.JSON)
                .body(new HikeUpdateRequest("Nom modifié", "Description modifiée"))
                .when()
                .patch("/{id}", hikeId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo("Nom modifié"))
                .body("description", equalTo("Description modifiée"));
    }

    @Test
    @DisplayName("Le créateur peut supprimer sa propre randonnée")
    void shouldDeleteOwnHike() {
        Integer hikeId = createHikeAs(tokenUserA);

        given()
                .header("Authorization", "Bearer " + tokenUserA)
                .when()
                .delete("/{id}/delete", hikeId)
                .then()
                .statusCode(HttpStatus.OK.value());

        assertFalse(hikeRepository.existsById(hikeId));
    }

    @Test
    @DisplayName("Un utilisateur peut ajouter une randonnée à ses favoris")
    void shouldAddHikeToFavorites() {
        Integer hikeId = createHikeAs(tokenUserA);

        given()
                .header("Authorization", "Bearer " + tokenUserB)
                .when()
                .put("/{id}/favorite", hikeId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .header("Authorization", "Bearer " + tokenUserB)
                .when()
                .get("/favorites")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content.id", hasItem(hikeId));
    }

    @Test
    @DisplayName("Ajouter deux fois le même favori reste idempotent")
    void shouldBeIdempotentWhenAddingFavoriteTwice() {
        Integer hikeId = createHikeAs(tokenUserA);

        given().header("Authorization", "Bearer " + tokenUserB)
                .put("/{id}/favorite", hikeId).then().statusCode(HttpStatus.NO_CONTENT.value());
        given().header("Authorization", "Bearer " + tokenUserB)
                .put("/{id}/favorite", hikeId).then().statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .header("Authorization", "Bearer " + tokenUserB)
                .when()
                .get("/favorites")
                .then()
                .body("totalElements", equalTo(1));
    }

    @Test
    @DisplayName("Un utilisateur peut retirer une randonnée de ses favoris")
    void shouldRemoveHikeFromFavorites() {
        Integer hikeId = createHikeAs(tokenUserA);

        given().header("Authorization", "Bearer " + tokenUserB)
                .put("/{id}/favorite", hikeId).then().statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .header("Authorization", "Bearer " + tokenUserB)
                .when()
                .delete("/{id}/favorite", hikeId)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .header("Authorization", "Bearer " + tokenUserB)
                .when()
                .get("/favorites")
                .then()
                .body("content.id", not(hasItem(hikeId)));
    }

    @Test
    @DisplayName("La liste des randonnées de l'utilisateur est paginée")
    void shouldReturnPaginatedUserHikes() {
        createHikeAs(tokenUserA);
        createHikeAs(tokenUserA);
        createHikeAs(tokenUserA);

        given()
                .header("Authorization", "Bearer " + tokenUserA)
                .queryParam("page", 0)
                .queryParam("size", 2)
                .when()
                .get("/mines")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content.size()", equalTo(2))
                .body("totalElements", equalTo(3))
                .body("totalPages", equalTo(2));
    }

    @Test
    @DisplayName("La liste des randonnées de l'utilisateur ne contient pas celles des autres")
    void shouldNotReturnOtherUsersHikesInMines() {
        createHikeAs(tokenUserA);
        createHikeAs(tokenUserB);

        given()
                .header("Authorization", "Bearer " + tokenUserA)
                .when()
                .get("/mines")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content.size()", equalTo(1));
    }
}