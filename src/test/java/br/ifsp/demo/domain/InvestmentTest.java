package br.ifsp.demo.domain;

import br.ifsp.demo.util.DateFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
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

    private static Stream<Arguments> getInvalidParameters() {
        return Stream.of(
                Arguments.of(0, new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1))),
                Arguments.of(-0.1, new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1))),
                Arguments.of(100.0, null)
        );
    }

    @ParameterizedTest
    @Tag("TDD")
    @Tag("UnitTest")
    @MethodSource("getInvalidParameters")
    @DisplayName("Should return error when try to create investment with invalid parameters")
    void shouldReturnErrorWhenTryToCreateInvestmentWithInvalidParameters(double initialValue, Asset asset){
        assertThrows(IllegalArgumentException.class, () -> {
            Investment investment = new Investment(initialValue, asset);
        });
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Should correctly return toString of an investment")
    void shouldCorrectlyReturnToStringOfAnInvestment(){
        Asset asset = new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1));
        Investment investment = new Investment(100, asset);
        String result = investment.toString();
        assertThat(result).isEqualTo(
                "Initial value = R$ 100,00 | Asset name = " + asset.getName() +
                        " | Type: " + asset.getAssetType() +
                        " | Asset profitability = " + String.format("%.2f%%", asset.getProfitability() * 100) +
                        " | Asset maturity date = " + DateFormatter.formatDateToSlash(asset.getMaturityDate()));
    }

    @Test
    @DisplayName("Should return initial value of the investment")
    void shouldReturnInitialValueOfTheInvestment(){
        Asset asset = new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1));
        Investment investment = new Investment(100, asset);
        assertThat(investment.getInitialValue()).isEqualTo(100.0);
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Should calculate future balance of an investment")
    void shouldCalculateFutureBalanceOfAnInvestment(){
        LocalDate purchaseDate = LocalDate.now();
        Asset asset = new Asset("Banco Inter", CDB, 0.1, LocalDate.now().plusMonths(2));
        Investment sut = new Investment(1000, asset, purchaseDate);

        double totalBalance = sut.calculateBalanceAt(sut.getMaturityDate());
        double expectedBalance = 1213.85;

        assertThat(totalBalance).isEqualTo(expectedBalance);
    }
}