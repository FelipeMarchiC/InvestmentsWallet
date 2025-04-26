package br.ifsp.demo.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AssetTest {

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Should correctly return toString of an Asset")
    void shouldCorrectlyReturnToStringOfAnAsset() {
        Asset asset = new Asset("PETR4", 0.01);
        String result = asset.toString();
        assertThat(result).isEqualTo("Asset name = PETR4 | Asset profitability = 0.01");
    }

    @ParameterizedTest
    @Tag("TDD")
    @Tag("UnitTest")
    @NullAndEmptySource
    @DisplayName("Should return error when name is null or empty")
    void shouldReturnErrorWhenNameIsNullOrEmpty(String name) {
        assertThrows(IllegalArgumentException.class, () -> {
            Asset asset = new Asset(name, 0.01);
        });
    }

    @ParameterizedTest
    @Tag("TDD")
    @Tag("UnitTest")
    @CsvSource({"PETR4, -0.01", "PETR4, 0.0"})
    @DisplayName("Should return error when profitability is invalid")
    void shouldReturnErrorWhenProfitabilityIsInvalid(String name, double profitability) {
        assertThrows(IllegalArgumentException.class, () -> {
            Asset asset = new Asset(name, profitability);
        });
    }
}