package br.ifsp.demo.service;

import br.ifsp.demo.domain.Asset;
import br.ifsp.demo.repository.AssetRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
}