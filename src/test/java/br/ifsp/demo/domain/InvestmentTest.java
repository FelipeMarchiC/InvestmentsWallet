package br.ifsp.demo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class InvestmentTest {

    @ParameterizedTest
    @Tag("TDD")
    @Tag("UnitTest")
    @CsvSource({"0.0, PETR4, 0.01", "-100.0, PETR4, 0.01"})
    @DisplayName("Should return error when try to create investment with invalid parameters")
    void shouldReturnErrorWhenTryToCreateInvestmentWithInvalidParameters(double initialValue, String assetName, double assetProfitability){
        assertThrows(IllegalArgumentException.class, () -> {
            Asset asset = new Asset(assetName, assetProfitability, LocalDate.now().plusYears(1));
            Investment investment = new Investment(initialValue, asset);
        });
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Should correctly return toString of an investment")
    void shouldCorrectlyReturnToStringOfAnInvestment(){
        Asset asset = new Asset("PETR4", 0.01, LocalDate.now().plusYears(1));
        Investment investment = new Investment(100, asset);
        String result = investment.toString();
        assertThat(result).isEqualTo(
                "Initial value = 100.0 | Asset name = " + asset.getName() +
                        " | Asset profitability = " + asset.getProfitability() +
                        " | Asset maturity date = " + asset.getMaturityDate());
    }
}