package br.ifsp.demo.service;

import br.ifsp.demo.domain.Asset;
import br.ifsp.demo.repository.AssetRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static br.ifsp.demo.domain.AssetType.CDB;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {
    @Mock
    AssetRepository assetRepository;
    @InjectMocks
    AssetService sut;

    @Test
    @Tag("UnitTest")
    @Tag("Structural")
    @DisplayName("Should throw exception when assetId is null")
    void shouldThrowExceptionWhenAssetIdIsNull(){
        assertThrows(NullPointerException.class, () -> {
            sut.getAssetById(null);
        }, "assetId cannot be null");
    }

    @Test
    @Tag("UnitTest")
    @Tag("Structural")
    @DisplayName("Should throw exception when assetId not found")
    void shouldThrowExceptionWhenAssetIdNotFound(){
        UUID assetId = UUID.randomUUID();
        when(assetRepository.findById(assetId)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> {
            sut.getAssetById(assetId);
        }, "Asset not found for id: " + assetId);
    }

    @Test
    @Tag("UnitTest")
    @Tag("Structural")
    @DisplayName("Should return asset")
    void shouldReturnAsset(){
        Asset asset = new Asset();
        when(assetRepository.findById(asset.getId())).thenReturn(Optional.of(asset));
        assertThat(sut.getAssetById(asset.getId())).isEqualTo(asset);
    }
    
    @Nested
    class MutationTests {
        @ParameterizedTest
        @Tag("UnitTest")
        @Tag("Mutation")
        @ValueSource(doubles = {-0.01, 0.0})
        @DisplayName("Should throw IllegalArgumentException when profitability is lower then 0")
        void shouldThrowIllegalArgumentExceptionWhenProfitabilityIsLowerOrEqual0(double profitability){
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                new Asset("Banco Inter", CDB, profitability, LocalDate.now());
            });

            String expectedMessage = "Asset profitability must be greater than zero";
            String actualMessage = exception.getMessage();

            assertThat(expectedMessage).isEqualTo(actualMessage);
        }
    }
}