package br.ifsp.demo.controller;

import br.ifsp.demo.domain.AssetType;
import br.ifsp.demo.dto.investment.InvestmentRequestDTO;
import br.ifsp.demo.dto.investment.InvestmentResponseDTO;
import br.ifsp.demo.security.auth.AuthRequest;
import br.ifsp.demo.security.auth.AuthResponse;
import br.ifsp.demo.security.auth.RegisterUserRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("IntegrationTest")
@DisplayName("Wallet API Integration Tests")
class WalletAPIControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    private final UUID tesouroDiretoAssetId = UUID.fromString("5bbff5c5-e4df-4e37-9f46-5cdc332f1f70");
    private final UUID cdbAssetId = UUID.fromString("cd63e59b-1fbf-4461-a03e-8d3449610b14");

    @BeforeAll
    void setupRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @BeforeEach
    void setupTestUserAndToken() {
        String uniqueEmail = "integration.user." + UUID.randomUUID() + "@example.com";
        String password = "securepass";

        given()
                .contentType(ContentType.JSON)
                .body(new RegisterUserRequest("Test", "User", uniqueEmail, password))
                .when()
                .post("/api/v1/register")
                .then()
                .statusCode(201);

        AuthResponse authResponse = given()
                .contentType(ContentType.JSON)
                .body(new AuthRequest(uniqueEmail, password))
                .when()
                .post("/api/v1/authenticate")
                .then()
                .statusCode(200)
                .extract()
                .as(AuthResponse.class);

        jwtToken = authResponse.token();
    }

    private UUID addInvestment(double initialValue, UUID assetId) throws Exception {
        List<InvestmentResponseDTO> initialInvestments = getActiveInvestments();

        InvestmentRequestDTO requestDTO = new InvestmentRequestDTO(initialValue, assetId);
        given()
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(ContentType.JSON)
                .body(requestDTO)
                .when()
                .post("/api/v1/wallet/investment")
                .then()
                .statusCode(201);

        List<InvestmentResponseDTO> currentInvestments = getActiveInvestments();
        assertThat(currentInvestments.size()).isEqualTo(initialInvestments.size() + 1);

        return currentInvestments.stream()
                .filter(inv -> initialInvestments.stream().noneMatch(existingInv -> existingInv.id().equals(inv.id())))
                .filter(inv -> Math.abs(inv.initialValue() - initialValue) < 0.001 && inv.assetId().equals(assetId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possível encontrar o investimento recém-adicionado na carteira.")).id();
    }

    private void withdrawInvestment(UUID investmentId) throws Exception {
        List<InvestmentResponseDTO> initialInvestments = getActiveInvestments();

        given()
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(ContentType.JSON)
                .when()
                .post("/api/v1/wallet/investment/withdraw/" + investmentId)
                .then()
                .statusCode(204);

        List<InvestmentResponseDTO> currentInvestments = getActiveInvestments();
        assertThat(currentInvestments.size()).isEqualTo(initialInvestments.size() - 1);
    }

    private List<InvestmentResponseDTO> getActiveInvestments() throws Exception {
        String responseContent = given()
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get("/api/v1/wallet/investment")
                .then()
                .statusCode(200)
                .extract()
                .asString();
        return objectMapper.readValue(responseContent, new TypeReference<>() {
        });
    }

    private List<InvestmentResponseDTO> getHistoryInvestments() throws Exception {
        String responseContent = given()
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get("/api/v1/wallet/history")
                .then()
                .statusCode(200)
                .extract()
                .asString();
        return objectMapper.readValue(responseContent, new TypeReference<>() {
        });

    }
    private void assertInvestmentMatchesResponse(InvestmentResponseDTO expected, Response response, String jsonPath) {
        String id = response.path(jsonPath + "id");
        Float initialValue = response.path(jsonPath + "initialValue");
        String assetId = response.path(jsonPath + "assetId");
        String purchaseDate = response.path(jsonPath + "purchaseDate");
        String withdrawDate = response.path(jsonPath + "withdrawDate");
        String walletId = response.path(jsonPath + "walletId");

        assertThat(id).isEqualTo(expected.id().toString());
        assertThat((double) initialValue).isEqualTo(expected.initialValue());
        assertThat(assetId).isEqualTo(expected.assetId().toString());
        assertThat(purchaseDate).isEqualTo(expected.purchaseDate().toString());

        if (expected.withdrawDate() == null) {
            assertThat(withdrawDate).isNull();
        } else {
            assertThat(withdrawDate).isEqualTo(expected.withdrawDate().toString());
        }

        assertThat(walletId).isEqualTo(expected.walletId().toString());
    }


    @Nested
    @Tag("ApiTest")
    @DisplayName("Investment Endpoints")
    class InvestmentEndpoints {

        @Test
        @DisplayName("DELETE /api/v1/wallet/investment/{investmentId}: Should successfully delete an investment")
        @Transactional
        void shouldSuccessfullyDeleteAnInvestment() throws Exception {
            UUID investmentIdToDelete = addInvestment(100.0, tesouroDiretoAssetId);

            given()
                    .header("Authorization", "Bearer " + jwtToken)
                    .when()
                    .delete("/api/v1/wallet/investment/{investmentId}", investmentIdToDelete)
                    .then()
                    .statusCode(204);

            List<InvestmentResponseDTO> currentActiveInvestments = getActiveInvestments();
            assertThat(currentActiveInvestments).noneMatch(inv -> inv.id().equals(investmentIdToDelete));

            List<InvestmentResponseDTO> currentHistoryInvestments = getHistoryInvestments();
            assertThat(currentHistoryInvestments).noneMatch(inv -> inv.id().equals(investmentIdToDelete));
        }

        @Test
        @DisplayName("POST /api/v1/wallet/investment/withdraw/{investmentId}: Should successfully withdraw an investment")
        @Transactional
        void shouldSuccessfullyWithdrawAnInvestment() throws Exception {
            UUID investmentIdToWithdraw = addInvestment(200.0, cdbAssetId);

            given()
                    .header("Authorization", "Bearer " + jwtToken)
                    .when()
                    .post("/api/v1/wallet/investment/withdraw/{investmentId}", investmentIdToWithdraw)
                    .then()
                    .statusCode(204);

            List<InvestmentResponseDTO> activeInvestments = getActiveInvestments();
            assertThat(activeInvestments).noneMatch(inv -> inv.id().equals(investmentIdToWithdraw));

            List<InvestmentResponseDTO> historyInvestments = getHistoryInvestments();
            assertThat(historyInvestments).anyMatch(inv -> inv.id().equals(investmentIdToWithdraw) && inv.withdrawDate() != null);
        }

        @Test
        @DisplayName("GET /api/v1/wallet/investment/{investmentId}: Should retrieve an investment by ID")
        @Transactional
        void shouldRetrieveAnInvestmentById() throws Exception {
            UUID investmentIdToRetrieve = addInvestment(300.0, tesouroDiretoAssetId);

            given()
                    .header("Authorization", "Bearer " + jwtToken)
                    .when()
                    .get("/api/v1/wallet/investment/{investmentId}", investmentIdToRetrieve)
                    .then()
                    .statusCode(200)
                    .body("id", notNullValue())
                    .body("initialValue", notNullValue());
        }

        @Test
        @DisplayName("GET /api/v1/wallet/investment/filterByType/{type} : Should filter active investments by type")
        @Transactional
        void shouldFilterActiveInvestmentsByType() throws Exception {
            UUID id1 = addInvestment(100.0, tesouroDiretoAssetId);
            UUID id2 = addInvestment(200.0, cdbAssetId);

            List<String> tesouroIds = given().header("Authorization", "Bearer " + jwtToken)
                    .when().get("/api/v1/wallet/investment/filterByType/{type}", AssetType.TESOURO_DIRETO)
                    .then().statusCode(200)
                    .contentType(ContentType.JSON)
                    .extract()
                    .jsonPath()
                    .getList("id", String.class);

            assertThat(tesouroIds)
                    .hasSize(1)
                    .containsExactly(id1.toString());

            List<String> cdbIds = given().header("Authorization", "Bearer " + jwtToken)
                    .when().get("/api/v1/wallet/investment/filterByType/{type}", AssetType.CDB)
                    .then().statusCode(200)
                    .contentType(ContentType.JSON)
                    .extract()
                    .jsonPath()
                    .getList("id", String.class);

            assertThat(cdbIds)
                    .hasSize(1)
                    .containsExactly(id2.toString());
        }

        @Test
        @DisplayName("GET /api/v1/wallet/investment/filterByType/{type} : Should filter active investments by type should return empty for filter active investments by type when no match")
        @Transactional
        void shouldReturnEmptyForFilterActiveInvestmentsByTypeWhenNoMatch() throws Exception {
            UUID id = addInvestment(50.0, tesouroDiretoAssetId);

            List<String> cdbIds = given().header("Authorization", "Bearer " + jwtToken)
                    .when().get("/api/v1/wallet/investment/filterByType/{type}", AssetType.CDB)
                    .then().statusCode(200)
                    .contentType(ContentType.JSON)
                    .extract()
                    .jsonPath()
                    .getList("id", String.class);

            assertThat(cdbIds).isEmpty();
        }

        @Test
        @DisplayName("GET /api/v1/wallet/investment/filterByDate: Should filter active investments by date")
        @Transactional
        void shouldFilterActiveInvestmentsByDate() throws Exception {

            UUID id1 = addInvestment(123.45, tesouroDiretoAssetId);
            UUID id2 = addInvestment(678.90, cdbAssetId);

            // intervalo que engloba hoje
            String from = LocalDate.now().minusDays(1).toString();
            String to   = LocalDate.now().plusDays(1).toString();

            List<String> ids = given()
                    .header("Authorization", "Bearer " + jwtToken)
                    .queryParam("initialDate", from)
                    .queryParam("finalDate", to)
                    .when()
                    .get("/api/v1/wallet/investment/filterByDate")
                    .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .extract()
                    .jsonPath()
                    .getList("id", String.class);
            assertThat(ids)
                    .hasSize(2)
                    .containsExactlyInAnyOrder(id1.toString(), id2.toString());
        }

        @Test
        @DisplayName("GET /api/v1/wallet/investment/filterByDate: Should return empty when no active investments in range")
        @Transactional
        void shouldReturnEmptyForFilterActiveInvestmentsByDateWhenNoMatch() throws Exception {
            addInvestment(50.0, tesouroDiretoAssetId);
            String from = LocalDate.now().plusDays(1).toString();
            String to   = LocalDate.now().plusDays(2).toString();

            given()
                    .header("Authorization", "Bearer " + jwtToken)
                    .queryParam("initialDate", from)
                    .queryParam("finalDate", to)
                    .when()
                    .get("/api/v1/wallet/investment/filterByDate")
                    .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("size()", is(0));
        }
    }

    @Nested
    @Tag("ApiTest")
    @DisplayName("Wallet Report and Balance Endpoints")
    class WalletReportAndBalanceEndpoints {

        @Test
        @DisplayName("GET /api/v1/wallet/report: Should generate a wallet report")
        @Transactional
        void shouldGenerateAWalletReport() throws Exception {
            addInvestment(100.0, tesouroDiretoAssetId);
            UUID withdrawnInvestmentId = addInvestment(50.0, cdbAssetId);
            given()
                    .header("Authorization", "Bearer " + jwtToken)
                    .when()
                    .post("/api/v1/wallet/investment/withdraw/{investmentId}", withdrawnInvestmentId)
                    .then()
                    .statusCode(204);

            String reportContent = given()
                    .header("Authorization", "Bearer " + jwtToken)
                    .when()
                    .get("/api/v1/wallet/report")
                    .then()
                    .statusCode(200)
                    .extract()
                    .asString();

            assertThat(reportContent).contains("=========== WALLET REPORT ===========");
            assertThat(reportContent).contains("> Active Investments:");
            assertThat(reportContent).contains("> Historical Investments:");
            assertThat(reportContent).contains("> Current Total Balance:");
            assertThat(reportContent).contains("> Future Investments Balance:");
            assertThat(reportContent).contains("> Investment by Type:");
            assertThat(reportContent).contains("Tesouro Selic 2025");
            assertThat(reportContent).contains("CDB Banco Inter");
        }

        @Test
        @DisplayName("GET /api/v1/wallet/totalBalance: Should retrieve total wallet balance")
        @Transactional
        void shouldRetrieveTotalBalance() throws Exception {
            addInvestment(100.0, tesouroDiretoAssetId);
            addInvestment(200.0, cdbAssetId);

            Double totalBalance = given()
                    .header("Authorization", "Bearer " + jwtToken)
                    .when()
                    .get("/api/v1/wallet/totalBalance")
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(Double.class);

            assertThat(totalBalance).isEqualTo(300.0);
        }

        @Test
        @DisplayName("GET /api/v1/wallet/futureBalance: Should retrieve future wallet balance")
        @Transactional
        void shouldRetrieveFutureBalance() throws Exception {
            addInvestment(100.0, tesouroDiretoAssetId);
            addInvestment(200.0, cdbAssetId);

            Double futureBalance = given()
                    .header("Authorization", "Bearer " + jwtToken)
                    .when()
                    .get("/api/v1/wallet/futureBalance")
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(Double.class);

            assertThat(futureBalance).isPositive();
            assertThat(futureBalance).isGreaterThan(300.0);
        }
    }

    @DisplayName("Wallet History Tests")
    @Nested
    class WalletHistoryTests{
        @Test
        @DisplayName("GET /api/v1/wallet/history: should retrieve only withdrawn investments")
        @Transactional
        void shouldRetrieveWalletHistory() throws Exception {

            UUID id1 = addInvestment(100.0, tesouroDiretoAssetId);
            UUID id2 = addInvestment(200.0, cdbAssetId);


            given()
                    .header("Authorization", "Bearer " + jwtToken)
                    .when()
                    .post("/api/v1/wallet/investment/withdraw/{investmentId}", id1)
                    .then()
                    .statusCode(204);


            given()
                    .header("Authorization", "Bearer " + jwtToken)
                    .when()
                    .get("/api/v1/wallet/history")
                    .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("size()", is(1))
                    .body("[0].id", equalTo(id1.toString()))
                    .body("[0].withdrawDate", notNullValue());
        }

        @Test
        @DisplayName("GET /api/v1/wallet/history/filterByType/{type}: Should retrieve history by type")
        @Transactional
        void shouldFilterHistoryByInvestmentType() throws Exception {
            UUID id2 = addInvestment(200.0, cdbAssetId);

            given().header("Authorization", "Bearer " + jwtToken)
                    .when().post("/api/v1/wallet/investment/withdraw/{investmentId}", id2)
                    .then().statusCode(204);

            given().header("Authorization", "Bearer " + jwtToken)
                    .when().get("/api/v1/wallet/history/filterByType/{type}", AssetType.CDB)
                    .then().statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("size()", is(1))
                    .body("[0].id", equalTo(id2.toString()));
        }

        @Test
        @DisplayName("GET /api/v1/wallet/history/filterByDate : should filter history by date")
        @Transactional
        void shouldFilterHistoryByDate() throws Exception {
            UUID id1 = addInvestment(100.0, tesouroDiretoAssetId);
            UUID id2 = addInvestment(200.0, cdbAssetId);

            given().header("Authorization", "Bearer " + jwtToken)
                    .when().post("/api/v1/wallet/investment/withdraw/{investmentId}", id1)
                    .then().statusCode(204);

            given().header("Authorization", "Bearer " + jwtToken)
                    .when().post("/api/v1/wallet/investment/withdraw/{investmentId}", id2)
                    .then().statusCode(204);

            String from = LocalDate.now().minusDays(1).toString();
            String to   = LocalDate.now().plusDays(1).toString();

            List<String> ids = given().header("Authorization", "Bearer " + jwtToken)
                    .queryParam("initialDate", from)
                    .queryParam("finalDate", to)
                    .when().get("/api/v1/wallet/history/filterByDate")
                    .then().statusCode(200)
                    .contentType(ContentType.JSON)
                    .extract()
                    .jsonPath()
                    .getList("id", String.class);

            assertThat(ids)
                    .hasSize(2)
                    .containsExactlyInAnyOrder(id1.toString(), id2.toString());
        }

        @Test
        @DisplayName("GET /api/v1/wallet/history/filterByDate with no matching dates")
        @Transactional
        void shouldReturnEmptyWhenNoHistoryInRange() throws Exception {
            UUID id = addInvestment(150.0, tesouroDiretoAssetId);
            given().header("Authorization", "Bearer " + jwtToken)
                    .when().post("/api/v1/wallet/investment/withdraw/{investmentId}", id)
                    .then().statusCode(204);

            String from = LocalDate.now().plusDays(2).toString();
            String to   = LocalDate.now().plusDays(3).toString();

            given().header("Authorization", "Bearer " + jwtToken)
                    .queryParam("initialDate", from)
                    .queryParam("finalDate", to)
                    .when().get("/api/v1/wallet/history/filterByDate")
                    .then().statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("size()", is(0));
        }
    }

    @Nested
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Wallet and Investment Create & Retrieve Tests")
    class WalletAndInvestmentTests {

        private InvestmentRequestDTO investmentRequest() {
            return new InvestmentRequestDTO(150.0, tesouroDiretoAssetId);
        }

        private InvestmentRequestDTO investmentRequest2() {
            return new InvestmentRequestDTO(150.0, cdbAssetId);
        }

        @Nested
        @DisplayName("User Authentication")
        class UserAuthentication {
            @Test
            @DisplayName("GET /api/v1/wallet: should return 401 when user is not authenticated")
            void getWalletShouldReturnUnauthorizedWhenUserIsNotAuthenticated () {
                given()
                        .when()
                        .get("/api/v1/wallet")
                        .then()
                        .statusCode(401);
            }

            @Test
            @DisplayName("GET /api/v1/wallet: should return 401 when token is invalid")
            void getWalletShouldReturnUnauthorizedWhenTokenIsInvalid() {
                given()
                        .when()
                        .get("/api/v1/wallet")
                        .then()
                        .statusCode(401);
            }

            @Test
            @DisplayName("POST /api/v1/wallet: should return 401 when user is not authenticated")
            void postWalletShouldReturnUnauthorizedWhenUserIsNotAuthenticated () {
                given()
                        .when()
                        .post("/api/v1/wallet")
                        .then()
                        .statusCode(401);
            }

            @Test
            @DisplayName("POST /api/v1/wallet/investment: should return 401 when user is not authenticated")
            void postInvestmentShouldReturnUnauthorizedWhenUserIsNotAuthenticated () {
                given()
                        .when()
                        .post("/api/v1/wallet/investment")
                        .then()
                        .statusCode(401);
            }
        }

        @Nested
        @DisplayName("Valid Operations")
        class ValidOperations {
            @Test
            @DisplayName("GET /api/v1/wallet: should retrieve wallet successfully with investments and history")
            @Transactional
            void shouldRetrieveWalletSuccessfullyWithInvestmentsAndHistory() throws Exception {
                addInvestment(investmentRequest().initialValue(), investmentRequest().assetId());
                addInvestment(investmentRequest2().initialValue(), investmentRequest2().assetId());
                withdrawInvestment(addInvestment(investmentRequest().initialValue(), investmentRequest().assetId()));

                var actives = getActiveInvestments();
                var history = getHistoryInvestments();

                Response response = given()
                        .header("Authorization", "Bearer " + jwtToken)
                        .when()
                        .get("/api/v1/wallet")
                        .then()
                        .statusCode(200)
                        .contentType(ContentType.JSON)
                        .extract()
                        .response();

                for (int i = 0; i < actives.size(); i++) {
                    assertInvestmentMatchesResponse(actives.get(i), response, "investments[" + i + "].");
                }

                for (int i = 0; i < history.size(); i++) {
                    assertInvestmentMatchesResponse(history.get(i), response, "history[" + i + "].");
                }
            }

            @Test
            @DisplayName("GET /api/v1/wallet: should retrieve wallet with no investments successfully")
            @Transactional
            void shouldRetrieveWalletWithNoInvestmentsSuccessfully() throws Exception {
                var actives = getActiveInvestments();
                var history = getHistoryInvestments();

                assertThat(actives).isEmpty();
                assertThat(history).isEmpty();

                Response response = given()
                        .header("Authorization", "Bearer " + jwtToken)
                        .when()
                        .get("/api/v1/wallet")
                        .then()
                        .statusCode(200)
                        .contentType(ContentType.JSON)
                        .extract()
                        .response();

                String walletId = response.path("id");
                List<?> investments = response.path("investments");
                List<?> historyInvestments = response.path("history");

                assertThat(walletId).isNotNull();
                assertThat(investments).isEmpty();
                assertThat(historyInvestments).isEmpty();
            }

            @Test
            @DisplayName("POST /api/v1/wallet/investment: should create investment successfully")
            @Transactional
            void shouldCreateInvestmentSuccessfully() {
                given()
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(ContentType.JSON)
                        .body(investmentRequest())
                        .when()
                        .post("/api/v1/wallet/investment")
                        .then()
                        .statusCode(201);
            }

            @Test
            @DisplayName("GET /api/v1/wallet/investment/{id}: should return investment successfully")
            @Transactional
            void shouldReturnInvestmentSuccessfully() throws Exception {
                UUID investmentId = addInvestment(investmentRequest().initialValue(), investmentRequest().assetId());
                var actives = getActiveInvestments();
                var expected = actives.getFirst();

                assertThat(actives.size()).isEqualTo(1);

                Response response = given()
                        .header("Authorization", "Bearer " + jwtToken)
                        .when()
                        .get("/api/v1/wallet/investment/" + investmentId)
                        .then()
                        .statusCode(200)
                        .contentType(ContentType.JSON)
                        .extract()
                        .response();

                assertInvestmentMatchesResponse(expected, response, "");
            }
        }

        @Nested
        @DisplayName("Error Responses")
        class ErrorResponses {
            @Test
            @DisplayName("POST /api/v1/wallet: Should return 409 Conflict if wallet already exists")
            @Transactional
            void shouldFailToCreateWalletIfAlreadyExists() {
                given()
                        .header("Authorization", "Bearer " + jwtToken)
                        .when()
                        .post("/api/v1/wallet")
                        .then()
                        .statusCode(409);
            }

            @Test
            @DisplayName("GET /api/v1/wallet/investment/{id}: should return 404 when investment is not found")
            void shouldReturnNotFoundWhenInvestmentDoesNotExist() {
                UUID nonExistentId = UUID.fromString("00000000-0000-0000-0000-000000000000");

                given()
                        .header("Authorization", "Bearer " + jwtToken)
                        .when()
                        .get("/api/v1/wallet/investment/" + nonExistentId)
                        .then()
                        .statusCode(404);
            }

            @Test
            @DisplayName("GET /api/v1/wallet/investment/{id}: should return 404 when accessing others investment")
            void shouldReturnNotFoundWhenAccessingOthersInvestment() throws Exception {
                String otherUserJwt = jwtToken;
                setupTestUserAndToken();
                UUID investmentId = addInvestment(investmentRequest().initialValue(), investmentRequest().assetId());

                given()
                        .header("Authorization", "Bearer " + otherUserJwt)
                        .when()
                        .get("/api/v1/wallet/investment/" + investmentId)
                        .then()
                        .statusCode(404);
            }

            @Test
            @DisplayName("POST /api/v1/wallet/investment: should return 400 Bad Request when initialValue is 0")
            @Transactional
            void shouldReturnBadRequestWhenInitialValueIsZero() {
                given()
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(ContentType.JSON)
                        .body(new InvestmentRequestDTO(0.0, cdbAssetId))
                        .when()
                        .post("/api/v1/wallet/investment")
                        .then()
                        .statusCode(400);
            }

            @Test
            @DisplayName("POST /api/v1/wallet/investment: should return 400 Bad Request when initialValue is negative")
            @Transactional
            void shouldReturnBadRequestWhenInitialValueIsNegative() {
                given()
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(ContentType.JSON)
                        .body(new InvestmentRequestDTO(-0.1, cdbAssetId))
                        .when()
                        .post("/api/v1/wallet/investment")
                        .then()
                        .statusCode(400);
            }
        }

    }
}