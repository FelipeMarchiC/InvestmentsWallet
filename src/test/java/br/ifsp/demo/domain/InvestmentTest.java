package br.ifsp.demo.domain;

import br.ifsp.demo.util.DateFormatter;
import br.ifsp.demo.util.EffectiveWithdrawDateResolver;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static br.ifsp.demo.domain.AssetType.CDB;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InvestmentTest {

    @Nested
    class ValidInvestmentParameters {
        private static Stream<Arguments> getInvalidParameters() {
            return Stream.of(
                    Arguments.of(100.0, new Asset("Banco Inter", CDB, 0.01, LocalDate.now()), LocalDate.now().plusDays(1)),
                    Arguments.of(0, new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusMonths(2)), LocalDate.now()),
                    Arguments.of(-0.1, new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusMonths(2)), LocalDate.now()),
                    Arguments.of(100.0, null, LocalDate.now())
            );
        }

        @ParameterizedTest
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @MethodSource("getInvalidParameters")
        @DisplayName("Should return error when try to create investment with invalid parameters")
        void shouldReturnErrorWhenTryToCreateInvestmentWithInvalidParameters(double initialValue, Asset asset, LocalDate purchaseDate) {
            assertThrows(IllegalArgumentException.class, () -> {
                Investment investment = new Investment(initialValue, asset, purchaseDate);
            });
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should correctly return toString of an investment")
        void shouldCorrectlyReturnToStringOfAnInvestment() {
            Asset asset = new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1));
            Investment investment = new Investment(100, asset);
            String result = investment.toString();
            assertThat(result).isEqualTo(
                    "Initial value = R$ 100,00 | Asset name = " + asset.getName() +
                            " | Type: " + asset.getAssetType() +
                            " | Asset profitability = " + String.format("%.2f%%", asset.getProfitability() * 100) +
                            " | Asset maturity date = " + DateFormatter.formatDateToSlash(asset.getMaturityDate()));
        }
    }

    @Nested
    class BalanceCalculation {

        @ParameterizedTest
        @MethodSource("investmentBalanceTestData")
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should calculate balance at given reference date correctly")
        void shouldCalculateBalanceAtGivenReferenceDateCorrectly(LocalDate purchaseDate, LocalDate maturityDate, double expectedBalance) {
            Asset asset = new Asset("Banco Inter", CDB, 0.1, maturityDate);

            EffectiveWithdrawDateResolver dateResolver = mock(EffectiveWithdrawDateResolver.class);
            when(dateResolver.resolve(null)).thenReturn(maturityDate);
            Investment sut = new Investment(1000, asset, purchaseDate, dateResolver);

            double calculatedBalance = sut.calculateCurrentBalance();
            assertThat(calculatedBalance).isEqualTo(expectedBalance);
        }

        static Stream<Arguments> investmentBalanceTestData() {
            int year = LocalDate.now().getYear();
            return Stream.of(
                    Arguments.of(LocalDate.of(year, 4, 1), LocalDate.of(year, 4, 30), 1096.51), // Same month
                    Arguments.of(LocalDate.of(year, 1, 15), LocalDate.of(year, 2, 15), 1103.50), // 1 month
                    Arguments.of(LocalDate.of(year, 2, 1), LocalDate.of(year, 3, 1), 1093.03), // Leap year
                    Arguments.of(LocalDate.of(year, 5, 10), LocalDate.of(year, 11, 10), 1794.22), // 6 months
                    Arguments.of(LocalDate.of(year, 12, 15), LocalDate.of(year + 1, 1, 15), 1103.50)  // Year change and 31-day month
            );
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should use withdrawDate as effective date")
        void shouldUseWithdrawDateAsEffectiveDate() {
            int year = LocalDate.now().getYear();
            LocalDate purchaseDate = LocalDate.of(year, 4, 1);
            LocalDate withdrawDate = LocalDate.of(year, 5, 1);
            LocalDate maturityDate = LocalDate.of(year, 6, 1);

            Asset asset = new Asset("Banco Inter", CDB, 0.10, maturityDate);

            Investment investment = new Investment(1000.00, asset, purchaseDate);
            investment.setWithdrawDate(withdrawDate);

            double balance = investment.calculateCurrentBalance();
            assertThat(balance).isEqualTo(1100.00);
        }
    }

    @Nested
    class StructuralTests {
        @Test
        @Tag("Structural")
        @Tag("UnitTest")
        @DisplayName("ShouldThrowIllegalArgumentExceptionWhenPurchaseDateIsNull")
        void shouldThrowIllegalArgumentExceptionWhenPurchaseDateIsNull(){
            int year = LocalDate.now().getYear();
            LocalDate maturityDate = LocalDate.of(year, 6, 1);
            assertThrows(IllegalArgumentException.class, () -> {
                Asset asset = new Asset("Banco Inter", CDB, 0.10, maturityDate);
                Investment investment = new Investment(1000.00, asset, null);
            });
        }

        @Test
        @Tag("Structural")
        @Tag("UnitTest")
        @DisplayName("ShouldBeDifferentIfComparesWithAnotherClass")
        void shouldBeDifferentIfComparesWithAnotherClass(){
            int year = LocalDate.now().getYear();
            LocalDate maturityDate = LocalDate.of(year, 6, 1);
            LocalDate purchaseDate = LocalDate.of(year, 4, 1);
            Asset asset = new Asset("Banco Inter", CDB, 0.10, maturityDate);
            Investment investment = new Investment(1000.00, asset, purchaseDate);
            assertThat(investment.equals(new Object())).isFalse();
        }

        @Test
        @Tag("Structural")
        @Tag("UnitTest")
        @DisplayName("ShouldBeDifferentIfComparesWithNull")
        void shouldBeDifferentIfComparesWithNull(){
            int year = LocalDate.now().getYear();
            LocalDate maturityDate = LocalDate.of(year, 6, 1);
            LocalDate purchaseDate = LocalDate.of(year, 4, 1);
            Asset asset = new Asset("Banco Inter", CDB, 0.10, maturityDate);
            Investment investment = new Investment(1000.00, asset, purchaseDate);
            assertThat(investment.equals(null)).isFalse();
        }

        @Test
        @Tag("Structural")
        @Tag("UnitTest")
        @DisplayName("ShouldCreateInvestmentWithNoParameters")
        void shouldCreateInvestmentWithNoParameters(){
            Investment investment = new Investment();
            assertThat(investment).isNotNull();
        }
    }

    @Nested
    class MutationTests {
        @ParameterizedTest
        @MethodSource("provideInvalidScenariosForInvestmentWithoutPurchaseDate")
        @DisplayName("Should validate constructors without purchaseDate")
        void shouldValidateConstructorWithoutPurchaseDate(double initialValue, Asset asset, String message) {
            assertThatThrownBy(() -> new Investment(initialValue, asset))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(message);
        }

        public static Stream<Arguments> provideInvalidScenariosForInvestmentWithoutPurchaseDate() {
            return Stream.of(
                    Arguments.of(-100, mock(Asset.class), "Initial value must be greater than zero"),
                    Arguments.of(0, mock(Asset.class), "Initial value must be greater than zero"),
                    Arguments.of(100, null, "Asset cannot be null")
            );
        }

        @ParameterizedTest
        @MethodSource("provideInvalidScenariosForInvestmentWithPurchaseDate")
        @DisplayName("Should validate constructor with purchaseDate")
        void shouldValidateConstructorWithPurchaseDate(double initialValue, Asset asset, LocalDate purchaseDate ,String message) {
            assertThatThrownBy(() -> new Investment(initialValue, asset, purchaseDate, mock(EffectiveWithdrawDateResolver.class)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(message);
        }

        public static Stream<Arguments> provideInvalidScenariosForInvestmentWithPurchaseDate() {
            int year = LocalDate.now().getYear();
            LocalDate maturityDate = LocalDate.of(year, 4, 1);
            LocalDate latePurchaseDate = LocalDate.of(year, 6, 1);
            Asset asset = new Asset("Banco Inter", CDB, 0.10, maturityDate);

            return Stream.of(
                    Arguments.of(100, mock(Asset.class), null,"Purchase date cannot be null"),
                    Arguments.of(100, asset, latePurchaseDate,"Purchase date cannot be after maturity date")
            );
        }
    }
}
