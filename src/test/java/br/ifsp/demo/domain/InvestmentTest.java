package br.ifsp.demo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class InvestmentTest {
    @ParameterizedTest
    @Tag("TDD")
    @Tag("UnitTest")
    @CsvSource({"0,0,", "-1,1,", "-1,-1,"})
    @DisplayName("Should return error when try to create investment with invalid parameters")
    void shouldReturnErrorWhenTryToCreateInvestmentWithInvalidParameters(double initialValue, double recurrentValue, Asset asset){
        assertThrows(IllegalArgumentException.class, () -> {
            Investment investment = new Investment(initialValue, recurrentValue, asset);
        });
    }
}