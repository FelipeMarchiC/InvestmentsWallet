package br.ifsp.demo.controller;

import br.ifsp.demo.security.auth.AuthRequest;
import br.ifsp.demo.security.auth.RegisterUserRequest;
import br.ifsp.demo.security.user.JpaUserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("IntegrationTest")
@DisplayName("User API Integration Tests")
public class UserApiControllerTest {
    @LocalServerPort
    private int port;

    @BeforeAll
    void setupRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Autowired
    private JpaUserRepository userRepository;

    @AfterEach
    @BeforeEach
    void tearDown() {
        userRepository.deleteAll();
    }

    RegisterUserRequest createDefaultUser() {
        RegisterUserRequest request = generateDefaultRegisterUserRequest();

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/register")
                .then()
                .statusCode(201);

        return request;
    }

    private RegisterUserRequest generateDefaultRegisterUserRequest() {
        return new RegisterUserRequest(
                "Tester",
                "Userton",
                "tester.userton@testerton.com",
                "securepass"
        );
    }

    @Nested
    @DisplayName("Valid Operations")
    class ValidOperations {
        @Test
        @DisplayName("POST /register: should create a new user successfully")
        void shouldRegisterUserSuccessfully() {
            RegisterUserRequest request = generateDefaultRegisterUserRequest();

            given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/api/v1/register")
                    .then()
                    .statusCode(201)
                    .body("id", notNullValue());
        }

        @Test
        @DisplayName("POST /authenticate: should authenticate user successfully")
        void shouldAuthenticateUserSuccessfully() {
            RegisterUserRequest request = createDefaultUser();

            given()
                    .contentType(ContentType.JSON)
                    .body(new AuthRequest(request.email(), request.password()))
                    .when()
                    .post("/api/v1/authenticate")
                    .then()
                    .statusCode(200)
                    .body("token", notNullValue());
        }
    }

    @Nested
    @DisplayName("Error Responses")
    class ErrorResponses {
        @Test
        @DisplayName("POST /register: should return 409 when email is already registered")
        void shouldReturnConflictWhenEmailAlreadyRegistered() {
            RegisterUserRequest request = createDefaultUser();

            given()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/api/v1/register")
                    .then()
                    .statusCode(409);
        }
    }
}
