package br.ifsp.demo.domain;

import br.ifsp.demo.repository.WalletRepository;
import br.ifsp.demo.security.user.User;
import br.ifsp.demo.service.WalletService;
import br.ifsp.demo.util.EffectiveWithdrawDateResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletTest {

    @Mock
    private WalletRepository repository;
    @InjectMocks
    private WalletService walletService;

    private Wallet sut;
    private LocalDate baseDate;
    private User user;

    @BeforeEach
    void setUp() {
        sut = new Wallet();
        baseDate = LocalDate.of(2025, 4, 25);
        user = new User();
        user.setId(UUID.randomUUID());
    }

    @Nested
    @DisplayName("Balance Calculation")
    class BalanceCalculation {

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should calculate total balance with active investments")
        void shouldCalculateTotalBalanceWithActiveInvestments() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.ofNullable(sut));

            LocalDate purchaseDate = baseDate.minusMonths(1).minusDays(10);
            Asset asset = new Asset("Banco Inter", AssetType.CDB, 0.1, baseDate.plusMonths(2));

            EffectiveWithdrawDateResolver dateResolver = mock(EffectiveWithdrawDateResolver.class);
            when(dateResolver.resolve(null)).thenReturn(baseDate);

            Investment investment = new Investment(1000, asset, purchaseDate, dateResolver);
            walletService.addInvestment(user.getId(), investment);
            double total = sut.getTotalBalance();

            assertThat(total).isEqualTo(1139.12);
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should calculate total balance with history investments")
        void shouldCalculateTotalBalanceWithHistoryInvestments() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.ofNullable(sut));

            LocalDate purchaseDate = baseDate.minusMonths(1).minusDays(10);
            Asset asset = new Asset("Banco Inter", AssetType.CDB, 0.1, baseDate.plusMonths(2));
            Investment investment = new Investment(1000, asset, purchaseDate);

            walletService.addInvestment(user.getId(), investment);
            walletService.withdrawInvestment(user.getId(), investment.getId(), baseDate);
            double total = sut.getTotalBalance();

            assertThat(total).isEqualTo(1139.12);
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should calculate total balance with no investments")
        void shouldCalculateTotalBalanceWithNoInvestments() {
            double total = sut.getTotalBalance();
            assertThat(total).isZero();
        }
    }

    @Nested
    @DisplayName("Future Balance Calculation")
    class FutureBalanceCalculation {

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should calculate future balance with active investments")
        void shouldCalculateFutureBalanceWithActiveInvestments() {
            when(repository.findByUser_Id(user.getId())).thenReturn(Optional.ofNullable(sut));

            LocalDate purchaseDate = baseDate;
            Asset asset = new Asset("Banco Inter", AssetType.CDB, 0.1, baseDate.plusMonths(2));
            Investment investment = new Investment(1000, asset, purchaseDate);

            walletService.addInvestment(user.getId(), investment);
            double futureBalance = sut.getFutureBalance();

            assertThat(futureBalance).isEqualTo(1213.85);
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should return zero when calculate future balance with no active investments")
        void shouldReturnZeroWhenNoActiveInvestments() {
            double futureBalance = sut.getFutureBalance();

            assertThat(futureBalance).isZero();
        }
    }

    @Nested
    class StructuralTests {
        @Test
        @Tag("Structural")
        @Tag("UnitTest")
        @DisplayName("Wallet should be equal the same wallet")
        void walletShouldBeEqualTheSameWallet(){
            assertThat(sut).isEqualTo(sut);
        }
        @Test

        @Tag("Structural")
        @Tag("UnitTest")
        @DisplayName("Wallet should be different null")
        void walletShouldBeDifferentNull(){
            assertThat(sut).isNotEqualTo(null);
        }

        @Test
        @Tag("Structural")
        @Tag("UnitTest")
        @DisplayName("Wallet should be different another class")
        void walletShouldBeDifferentAnotherClass(){
            assertThat(sut).isNotEqualTo(new Object());
        }
    }

    @Nested
    class MutationTests {
        @Test
        @Tag("Mutation")
        @Tag("UnitTest")
        @DisplayName("Should return false when compare 2 different wallets")
        void shouldReturnFalseWhenCompare2DifferentWallets(){
            Wallet wallet = new Wallet();
            assertThat(sut.equals(wallet)).isFalse();
        }

        @Test
        @Tag("Mutation")
        @Tag("UnitTest")
        @DisplayName("Wallet hashcode should be different of zero")
        void walletHashcodeShouldBeDifferentOfZero(){
            assertThat(sut.hashCode()).isNotZero();
        }
    }
}
