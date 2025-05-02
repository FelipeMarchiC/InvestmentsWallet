package br.ifsp.demo.domain;

import br.ifsp.demo.repository.InMemoryWalletRepository;
import br.ifsp.demo.repository.WalletRepository;
import br.ifsp.demo.service.WalletService;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static br.ifsp.demo.domain.AssetType.CDB;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class WalletTest {
    private Wallet wallet;
    private WalletService sut;
    private LocalDate date;

    @BeforeEach
    public void setUp() {
        wallet = new Wallet();
        WalletRepository inMemoryRepository = new InMemoryWalletRepository();
        inMemoryRepository.save(wallet);
        sut = new WalletService(inMemoryRepository);
        date = LocalDate.of(2025, 4, 25);
    }

    @Nested
    class BalanceCalculation {

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Should calculate total balance with active investments")
        void shouldCalculateTotalBalanceWithActiveInvestments(){
            LocalDate purchaseDate = date.minusMonths(1).minusDays(10);
            Asset asset = new Asset("Banco Inter", CDB, 0.1, date.plusMonths(2));
            Investment investment = new Investment(1000, asset, purchaseDate);

            sut.addInvestment(wallet.getId(), investment);
            double totalBalance = wallet.getTotalBalance(date);
            double expectedBalance = 1139.12;

            assertThat(totalBalance).isEqualTo(expectedBalance);
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Should calculate total balance with history investments")
        void shouldCalculateTotalBalanceWithHistoryInvestments(){
            LocalDate purchaseDate = date.minusMonths(1).minusDays(10);
            Asset asset = new Asset("Banco Inter", CDB, 0.1, date.plusMonths(2));
            Investment investment = new Investment(1000, asset, purchaseDate);

            sut.addInvestment(wallet.getId(), investment);
            sut.withdrawInvestment(wallet.getId(), investment.getId(), date);

            double totalBalance = wallet.getTotalBalance(null);
            double expectedBalance = 1139.12;

            assertThat(totalBalance).isEqualTo(expectedBalance);
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Should calculate total balance with no investments")
        void shouldCalculateTotalBalanceWithNoInvestments(){
            double totalBalance = wallet.getTotalBalance(null);
            double expectedBalance = 0.0;
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
            LocalDate purchaseDate = date;
            Asset asset = new Asset("Banco Inter", CDB, 0.1, date.plusMonths(2));
            Investment investment = new Investment(1000, asset, purchaseDate);

            wallet.addInvestment(investment);
            double futureBalance = wallet.getFutureBalance();
            double expectedBalance = 1213.85;

            assertThat(futureBalance).isEqualTo(expectedBalance);
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Should return zero when calculate future balance with no active investments")
        void shouldReturnZeroWhenCalculateFutureBalanceWithNoActiveInvestments(){
            double futureBalance = wallet.getFutureBalance();
            double expectedBalance = 0.0;

            assertThat(futureBalance).isEqualTo(expectedBalance);
        }
    }
}