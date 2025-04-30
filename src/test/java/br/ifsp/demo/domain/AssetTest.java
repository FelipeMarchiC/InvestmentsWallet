package br.ifsp.demo.domain;

import br.ifsp.demo.util.DateFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static br.ifsp.demo.domain.AssetType.CDB;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class AssetTest {

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Should correctly return toString of an Asset")
    void shouldCorrectlyReturnToStringOfAnAsset() {
        Asset asset = new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1));
        String result = asset.toString();
        assertThat(result)
                .isEqualTo(
                        "Asset name = Banco Inter " +
                                "| Type: CDB " +
                                "| Asset profitability = 1,00% " +
                                "| Asset maturity date = " +
                                DateFormatter.formatDateToSlash(asset.getMaturityDate()));
    }
    
    @Test
    @DisplayName("Should return IllegalArgumentException if type is null")
    void shouldReturnIllegalArgumentExceptionIfTypeIsNull(){
        assertThrows(IllegalArgumentException.class, () -> {
            Asset asset = new Asset("Banco Inter", null, 0.01, LocalDate.now().plusYears(1));
        });
    }

    @Nested
    class InvalidAssetFields {
        @ParameterizedTest
        @Tag("TDD")
        @Tag("UnitTest")
        @NullAndEmptySource
        @DisplayName("Should return error when name is null or empty")
        void shouldReturnErrorWhenNameIsNullOrEmpty(String name) {
            assertThrows(IllegalArgumentException.class, () -> {
                Asset asset = new Asset(name, CDB, 0.01, LocalDate.now().plusYears(1));
            });
        }

        @ParameterizedTest
        @Tag("TDD")
        @Tag("UnitTest")
        @CsvSource({"Banco Inter, -0.01", "Banco Inter, 0.0", "Banco Inter, 0.001"})
        @DisplayName("Should return error when profitability is invalid")
        void shouldReturnErrorWhenProfitabilityIsInvalid(String name, double profitability) {
            assertThrows(IllegalArgumentException.class, () -> {
                Asset asset = new Asset(name, CDB, profitability, LocalDate.now().plusYears(1));
            });
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Should return error when maturity date is in the past")
        void shouldReturnErrorWhenMaturityDateIsInThePast(){
            assertThrows(IllegalArgumentException.class, () -> {
                Asset asset = new Asset("Banco Inter", CDB, 0.01, LocalDate.now().minusDays(1));
            });
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Should return error when maturity date is null")
        void shouldReturnErrorWhenMaturityDateIsNull(){
            assertThrows(IllegalArgumentException.class, () -> {
                Asset asset = new Asset("Banco Inter", CDB, 0.01, null);
            });
        }

    }
}