package br.ifsp.demo.service;

import br.ifsp.demo.domain.Asset;
import br.ifsp.demo.domain.Investment;
import br.ifsp.demo.domain.InvestmentFactory;
import br.ifsp.demo.domain.Wallet;
import br.ifsp.demo.repository.InMemoryWalletRepository;
import br.ifsp.demo.repository.WalletRepository;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.junit.jupiter.api.Assertions.*;

class WalletServiceTest {
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
    class RegisterInvestment {

        @Test
        @Tag("UnitTest")
        @Tag("TDD")
        @DisplayName("Should register an investment")
        void shouldRegisterAnInvestment(){
            Wallet wallet = new Wallet();
            WalletRepository inMemoryRepository = new InMemoryWalletRepository();
            inMemoryRepository.save(wallet);
            WalletService sut = new WalletService(inMemoryRepository);

            Asset asset = new Asset("PETR4", 0.01, LocalDate.now().plusYears(1));
            Investment investment = new Investment(100, asset);

            boolean result = sut.addInvestment(wallet.getId(), investment);
            assertThat(result).isTrue();
        }
    }

    @Nested
    class WithdrawInvestment {

        @Test
        @Tag("UnitTest")
        @Tag("TDD")
        @DisplayName("Should withdraw an investment")
        void shouldWithdrawAnInvestment(){
            Wallet wallet = new Wallet();
            WalletRepository inMemoryRepository = new InMemoryWalletRepository();
            inMemoryRepository.save(wallet);
            WalletService sut = new WalletService(inMemoryRepository);

            Asset asset = new Asset("PETR4", 0.01, LocalDate.now().plusYears(1));
            Investment investment = new Investment(100, asset);
            sut.addInvestment(wallet.getId(), investment);

            boolean result = sut.withdrawInvestment(wallet.getId(), investment.getId());

            assertThat(result).isTrue();
        }
    }

    @Nested
    class Balance {

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Should calculate total balance with active investments")
        void shouldCalculateTotalBalanceWithActiveInvestments(){
            LocalDate purchaseDate = LocalDate.now().minusMonths(1).minusDays(10);
            Asset asset = new Asset("PETR4", 0.1, LocalDate.now().plusMonths(2));
            Investment investment = InvestmentFactory.createInvestmentWithPurchaseDate(1000, asset, purchaseDate);

            sut.addInvestment(wallet.getId(), investment);
            double totalBalance = wallet.getTotalBalance();
            double expectedBalance = 1139;

            assertThat(totalBalance).isCloseTo(expectedBalance, within(0.2));
        }
        
        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Should calculate total balance with history investments")
        void shouldCalculateTotalBalanceWithHistoryInvestments(){
            LocalDate purchaseDate = LocalDate.now().minusMonths(1).minusDays(10);
            Asset asset = new Asset("PETR4", 0.1, LocalDate.now().plusMonths(2));
            Investment investment = InvestmentFactory.createInvestmentWithPurchaseDate(1000, asset, purchaseDate);

            sut.addInvestment(wallet.getId(), investment);
            sut.withdrawInvestment(wallet.getId(), investment.getId());

            double totalBalance = wallet.getTotalBalance();
            double expectedBalance = 1139;

            assertThat(totalBalance).isCloseTo(expectedBalance, within(0.2));
        }
    }

    @Nested
    class GetInvestments {

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Should return all investments on wallet")
        void shouldReturnAllInvestmentsOnWallet() {
            Asset asset = new Asset("PETR4", 0.01, LocalDate.now().plusYears(1));
            Investment investment1 = new Investment(1000, asset);
            Investment investment2 = new Investment(1500, asset);

            sut.addInvestment(wallet.getId(), investment1);
            sut.addInvestment(wallet.getId(), investment2);

            List<Investment> result = sut.getInvestments(wallet.getId());
            assertThat(result.size()).isEqualTo(2);
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("shouldReturnAnEmptyListWhenThereIsNoInvestments")
        void shouldReturnAnEmptyListWhenThereIsNoInvestments(){
            List<Investment> result = sut.getInvestments(wallet.getId());
            assertThat(result).isEqualTo(List.of());
        }
    }
}