package br.ifsp.demo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class InvestmentTest {

    private static Stream<Arguments> getInvalidParameters() {
        return Stream.of(
                Arguments.of(-10, 50, new Asset("PETR4")),
                Arguments.of(100, -10, new Asset("PETR4")),
                Arguments.of(100, 50, null),
                Arguments.of(0, 0, null)
        );
    }

    @ParameterizedTest
    @Tag("TDD")
    @Tag("UnitTest")
    @MethodSource("getInvalidParameters")
    @DisplayName("Should return error when try to create investment with invalid parameters")
    void shouldReturnErrorWhenTryToCreateInvestmentWithInvalidParameters(double initialValue, double recurrentValue, Asset asset){
        assertThrows(IllegalArgumentException.class, () -> {
            Investment investment = new Investment(initialValue, recurrentValue, asset);
        });
    }



    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Should correctly return toString of an investment")
    void shouldCorrectlyReturnToStringOfAnInvestment(){
        Asset asset = new Asset("PETR4");
        Investment investment = new Investment(100, 50, asset);
        String result = investment.toString();
        assertThat(result).isEqualTo("Initial value = 100.0 | Recurrent value = 50.0 | Asset name = " + asset.getName());
    }
}