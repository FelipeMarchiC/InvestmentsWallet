package br.ifsp.demo.domain;

import br.ifsp.demo.util.DateFormatter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import java.time.LocalDate;

import static br.ifsp.demo.domain.AssetType.CDB;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.junit.jupiter.api.Assertions.*;

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
            Investment sut = new Investment(1000, asset, purchaseDate);

            double calculatedBalance = sut.calculateBalanceAt(maturityDate);
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
        @DisplayName("Should use withdrawDate instead of referenceDate if set")
        void shouldUseWithdrawDateWhenPresent() {
            int year = LocalDate.now().getYear();
            LocalDate purchaseDate = LocalDate.of(year, 4, 1);
            LocalDate withdrawDate = LocalDate.of(year, 5, 1);
            LocalDate maturityDate = LocalDate.of(year, 6, 1);

            Asset asset = new Asset("Banco Inter", CDB, 0.10, maturityDate);
            Investment investment = new Investment(1000.00, asset, purchaseDate);
            investment.setWithdrawDate(withdrawDate);

            double balance = investment.calculateBalanceAt(maturityDate);
            assertThat(balance).isEqualTo(1100.00);
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Should use referenceDate if withdrawDate not defined")
        void shouldUseReferenceDateIfWithdrawDateNotDefined() {
            int year = LocalDate.now().getYear();
            LocalDate purchaseDate = LocalDate.of(year, 4, 1);
            LocalDate maturityDate = LocalDate.of(year, 6, 1);

            Asset asset = new Asset("Banco Inter", CDB, 0.10, maturityDate);
            Investment investment = new Investment(1000.00, asset, purchaseDate);

            double balance = investment.calculateBalanceAt(maturityDate);
            assertThat(balance).isEqualTo(1213.85);
        }
    }
}
