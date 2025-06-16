package br.ifsp.demo.controller;

import br.ifsp.demo.repository.WalletRepository;
import br.ifsp.demo.security.user.JpaUserRepository;
import br.ifsp.demo.dto.investment.InvestmentRequestDTO;
import br.ifsp.demo.dto.investment.InvestmentResponseDTO;
import br.ifsp.demo.security.auth.AuthRequest;
import br.ifsp.demo.security.auth.AuthResponse;
import br.ifsp.demo.security.auth.RegisterUserRequest;
import br.ifsp.demo.security.auth.RegisterUserResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("IntegrationTest")
@DisplayName("Wallet API Integration Tests")
class WalletAPIControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static String jwtToken;

    private final UUID tesouroDiretoAssetId = UUID.fromString("5bbff5c5-e4df-4e37-9f46-5cdc332f1f70");
    private final UUID cdbAssetId = UUID.fromString("cd63e59b-1fbf-4461-a03e-8d3449610b14");

    @BeforeAll
    static void setupGlobalIntegrationTestEnvironment(
            @Autowired MockMvc staticMockMvc,
            @Autowired ObjectMapper staticObjectMapper,

            @Autowired WalletRepository staticWalletRepository,
            @Autowired JpaUserRepository staticUserRepository
    ) throws Exception {
        staticWalletRepository.deleteAll();
        staticUserRepository.deleteAll();

        MvcResult registerResult = staticMockMvc.perform(MockMvcRequestBuilders.post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(staticObjectMapper.writeValueAsString(new RegisterUserRequest("Integration", "User", "integration.user@example.com", "securepass"))))
                .andExpect(status().isCreated())
                .andReturn();

        staticObjectMapper.readValue(registerResult.getResponse().getContentAsString(), RegisterUserResponse.class);

        MvcResult authResult = staticMockMvc.perform(MockMvcRequestBuilders.post("/api/v1/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(staticObjectMapper.writeValueAsString(new AuthRequest("integration.user@example.com", "securepass"))))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = authResult.getResponse().getContentAsString();
        AuthResponse authResponse = staticObjectMapper.readValue(responseContent, AuthResponse.class);
        jwtToken = authResponse.token();
    }

    private UUID addInvestment(double initialValue, UUID assetId) throws Exception {
        List<InvestmentResponseDTO> initialInvestments = getActiveInvestments();

        InvestmentRequestDTO requestDTO = new InvestmentRequestDTO(initialValue, assetId);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/wallet/investment")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());

        List<InvestmentResponseDTO> currentInvestments = getActiveInvestments();
        assertThat(currentInvestments.size()).isEqualTo(initialInvestments.size() + 1);

        return currentInvestments.stream()
                .filter(inv -> initialInvestments.stream().noneMatch(existingInv -> existingInv.id().equals(inv.id())))
                .filter(inv -> Math.abs(inv.initialValue() - initialValue) < 0.001 && inv.assetId().equals(assetId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Não foi possível encontrar o investimento recém-adicionado na carteira.")).id();
    }

    private List<InvestmentResponseDTO> getActiveInvestments() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/wallet/investment")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
    }

    private List<InvestmentResponseDTO> getHistoryInvestments() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/wallet/history")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
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

            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/wallet/investment/{investmentId}", investmentIdToDelete)
                            .header("Authorization", "Bearer " + jwtToken))
                    .andExpect(status().isNoContent());

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

            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/wallet/investment/withdraw/{investmentId}", investmentIdToWithdraw)
                            .header("Authorization", "Bearer " + jwtToken))
                    .andExpect(status().isNoContent());

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

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/wallet/investment/{investmentId}", investmentIdToRetrieve)
                            .header("Authorization", "Bearer " + jwtToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(investmentIdToRetrieve.toString()))
                    .andExpect(jsonPath("$.initialValue").value(300.0));
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
            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/wallet/investment/withdraw/{investmentId}", withdrawnInvestmentId)
                    .header("Authorization", "Bearer " + jwtToken));

            MvcResult reportResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/wallet/report")
                            .header("Authorization", "Bearer " + jwtToken))
                    .andExpect(status().isOk())
                    .andReturn();

            String reportContent = reportResult.getResponse().getContentAsString();
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

            MvcResult totalBalanceResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/wallet/totalBalance")
                            .header("Authorization", "Bearer " + jwtToken))
                    .andExpect(status().isOk())
                    .andReturn();

            Double totalBalance = objectMapper.readValue(totalBalanceResult.getResponse().getContentAsString(), Double.class);
            assertThat(totalBalance).isEqualTo(300.0);
        }

        @Test
        @DisplayName("GET /api/v1/wallet/futureBalance: Should retrieve future wallet balance")
        @Transactional
        void shouldRetrieveFutureBalance() throws Exception {
            addInvestment(100.0, tesouroDiretoAssetId);
            addInvestment(200.0, cdbAssetId);

            MvcResult futureBalanceResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/wallet/futureBalance")
                            .header("Authorization", "Bearer " + jwtToken))
                    .andExpect(status().isOk())
                    .andReturn();

            Double futureBalance = objectMapper.readValue(futureBalanceResult.getResponse().getContentAsString(), Double.class);
            assertThat(futureBalance).isPositive();
            assertThat(futureBalance).isGreaterThan(300.0);
        }
    }

    //
    // [Felipe Endpoints]
    //
    @Test
    @DisplayName("POST /api/v1/wallet: Should create wallet successfully")
    @Transactional
    void shouldCreateWalletSuccessfully() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/wallet")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.investments").isArray())
                .andExpect(jsonPath("$.historyInvestments").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/wallet: Should retrieve wallet successfully after creation")
    @Transactional
    void shouldRetrieveWalletSuccessfullyAfterCreation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/wallet")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/wallet")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.investments").isArray())
                .andExpect(jsonPath("$.historyInvestments").isArray());
    }
}