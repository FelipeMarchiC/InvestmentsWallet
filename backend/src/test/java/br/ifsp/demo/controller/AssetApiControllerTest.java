package br.ifsp.demo.controller;

import br.ifsp.demo.domain.Asset;
import br.ifsp.demo.repository.AssetRepository;
import br.ifsp.demo.repository.WalletRepository;
import br.ifsp.demo.security.auth.AuthRequest;
import br.ifsp.demo.security.auth.AuthResponse;
import br.ifsp.demo.security.auth.RegisterUserRequest;
import br.ifsp.demo.security.auth.RegisterUserResponse;
import br.ifsp.demo.security.user.JpaUserRepository;
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

import java.util.UUID;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("IntegrationTest")
@DisplayName("Asset API Integration Tests")
public class AssetApiControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private static String jwtToken;

    @BeforeAll
    static void setupGlobalIntegrationTestEnvironment(
            @Autowired MockMvc staticMockMvc,
            @Autowired ObjectMapper staticObjectMapper,

            @Autowired AssetRepository staticAssetRepository,
            @Autowired JpaUserRepository staticUserRepository
    ) throws Exception {
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
    @Test
    @DisplayName("GET /api/v1/asset: should retrive all assets")
    @Transactional
    void shouldRetrieveAllAssets() throws Exception {


        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/asset")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

}
