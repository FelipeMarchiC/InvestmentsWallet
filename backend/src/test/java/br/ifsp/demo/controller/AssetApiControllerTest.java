package br.ifsp.demo.controller;

import br.ifsp.demo.domain.Asset;
import br.ifsp.demo.repository.AssetRepository;
import br.ifsp.demo.security.auth.AuthRequest;
import br.ifsp.demo.security.auth.AuthResponse;
import br.ifsp.demo.security.auth.RegisterUserRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.transaction.annotation.Transactional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("IntegrationTest")
@DisplayName("Asset API Integration Tests (RestAssured)")
public class AssetApiControllerTest {

    @LocalServerPort
    private int port;

    private String jwtToken;

    @Autowired
    private AssetRepository assetRepository;

    @BeforeAll
    void setupRestAssuredAndAuth() throws Exception {

        RestAssured.port = port;
        RestAssured.basePath = "/api/v1";


        assetRepository.deleteAll();

        // registra usuário
        jwtToken =
                given()
                        .contentType(ContentType.JSON)
                        .body(new RegisterUserRequest("Integration", "User", "integration.user@example.com", "securepass"))
                        .when()
                        .post("/register")
                        .then()
                        .statusCode(201)
                        .extract()
                        .asString(); // extrai o corpo só pra ter certeza do 201; token vai no próximo passo


        AuthResponse auth =
                given()
                        .contentType(ContentType.JSON)
                        .body(new AuthRequest("integration.user@example.com", "securepass"))
                        .when()
                        .post("/authenticate")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(AuthResponse.class);

        jwtToken = auth.token();
    }

    @Test
    @DisplayName("GET /asset: should retrieve all assets")
    @Transactional
    void shouldRetrieveAllAssets() {
        given()
                .auth().oauth2(jwtToken)
                .when()
                .get("/asset")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", isA(java.util.List.class)); // verifica que é um array JSON
    }
}
