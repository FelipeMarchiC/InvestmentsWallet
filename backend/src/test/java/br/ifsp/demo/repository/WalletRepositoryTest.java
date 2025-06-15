package br.ifsp.demo.repository;

import br.ifsp.demo.domain.Wallet;
import br.ifsp.demo.security.user.JpaUserRepository;
import br.ifsp.demo.security.user.Role;
import br.ifsp.demo.security.user.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("IntegrationTest")
@Tag("PersistenceTest")
class WalletRepositoryTest {
    @Autowired private WalletRepository repository;
    @Autowired private JpaUserRepository userRepository;

    private UUID userId;

    @BeforeEach
    void setup() {
        repository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        user.setName("Felipe");
        user.setLastname("Gabriel");
        user.setEmail("felipe.gabriel@gmail.com");
        user.setPassword("senha123");
        user.setRole(Role.USER);
        userRepository.save(user);

        userId = user.getId();

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        repository.save(wallet);
    }

    @AfterEach void tearDown() {
        repository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should find walled by user id")
    void shouldFindWalletByUserId() {
        Optional<Wallet> result = repository.findByUser_Id(userId);
        assertThat(result).isPresent();
        assertThat(result.get().getUser().getId()).isEqualTo(userId);
    }
}