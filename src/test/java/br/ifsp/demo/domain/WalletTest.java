package br.ifsp.demo.domain;

import br.ifsp.demo.repository.InMemoryWalletRepository;
import br.ifsp.demo.repository.WalletRepository;
import br.ifsp.demo.service.WalletService;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class WalletTest {
    private Wallet wallet;
    private WalletService sut;

    @BeforeEach
    public void setUp() {
        wallet = new Wallet();
        WalletRepository inMemoryRepository = new InMemoryWalletRepository();
        inMemoryRepository.save(wallet);
        sut = new WalletService(inMemoryRepository);
    }

    @Nested
    class BalanceCalculation {

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Should calculate total balance with active investments")
        void shouldCalculateTotalBalanceWithActiveInvestments(){
            LocalDate purchaseDate = LocalDate.now().minusMonths(1).minusDays(10);
            Asset asset = new Asset("PETR4", 0.1, LocalDate.now().plusMonths(2));
            Investment investment = new Investment(1000, asset, purchaseDate);

            sut.addInvestment(wallet.getId(), investment);
            double totalBalance = wallet.getTotalBalance();
            double expectedBalance = 1139.12;

            assertThat(totalBalance).isEqualTo(expectedBalance);
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Should calculate total balance with history investments")
        void shouldCalculateTotalBalanceWithHistoryInvestments(){
            LocalDate purchaseDate = LocalDate.now().minusMonths(1).minusDays(10);
            Asset asset = new Asset("PETR4", 0.1, LocalDate.now().plusMonths(2));
            Investment investment = new Investment(1000, asset, purchaseDate);

            sut.addInvestment(wallet.getId(), investment);
            sut.withdrawInvestment(wallet.getId(), investment.getId());

            double totalBalance = wallet.getTotalBalance();
            double expectedBalance = 1139.12;

            assertThat(totalBalance).isEqualTo(expectedBalance);
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Should calculate total balance with no investments")
        void shouldCalculateTotalBalanceWithNoInvestments(){
            double totalBalance = wallet.getTotalBalance();
            double expectedBalance = 0;
            assertThat(totalBalance).isEqualTo(expectedBalance);
        }
    }

    @Nested
    class FutureBalanceCalculation {

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Should calculate future balance with active investments")
        void shouldCalculateFutureBalanceWithActiveInvestments(){
            LocalDate purchaseDate = LocalDate.now();
            Asset asset = new Asset("PETR4", 0.1, LocalDate.now().plusMonths(2));
            Investment investment = new Investment(1000, asset, purchaseDate);

            wallet.addInvestiment(investment);
            double futureBalance = wallet.getFutureBalance();
            double expectedBalance = 1213.85;

            assertThat(futureBalance).isEqualTo(expectedBalance);
        }
    }
}