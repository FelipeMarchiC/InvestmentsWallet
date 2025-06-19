package br.ifsp.demo.controller;

import br.ifsp.demo.repository.AssetRepository;
import br.ifsp.demo.security.auth.AuthRequest;
import br.ifsp.demo.security.auth.AuthResponse;
import br.ifsp.demo.security.auth.RegisterUserRequest;
import br.ifsp.demo.security.user.JpaUserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("IntegrationTest")
@DisplayName("Asset API Integration Tests (RestAssured)")
class AssetApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private AssetRepository assetRepository;

    private String jwtToken;

    @BeforeAll
    void setupRestAssured() {
        RestAssured.baseURI  = "http://localhost";
        RestAssured.port     = port;
        RestAssured.basePath = "/api/v1";
    }

    @BeforeEach
    void cleanDatabaseAndRegisterUser() {
        userRepository.deleteAll();
        assetRepository.deleteAll();

        String email    = "integration.user." + UUID.randomUUID() + "@example.com";
        String password = "securepass";

        given()
                .contentType(ContentType.JSON)
                .body(new RegisterUserRequest("Integration", "User", email, password))
                .when()
                .post("/register")
                .then()
                .statusCode(201);

        AuthResponse authResponse = given()
                .contentType(ContentType.JSON)
                .body(new AuthRequest(email, password))
                .when()
                .post("/authenticate")
                .then()
                .statusCode(200)
                .extract()
                .as(AuthResponse.class);

        jwtToken = authResponse.token();
    }

    @Test
    @DisplayName("GET /asset: should retrieve all assets")
    void shouldRetrieveAllAssets() {
        given()
                .auth().oauth2(jwtToken)
                .when()
                .get("/asset")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", isA(java.util.List.class));
    }
}
