package br.ifsp.demo.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class AssetServiceTest {
    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Should return an empty list if there is no asset registered")
    void shouldReturnAnEmptyListIfThereIsNoAssetRegistered(){
        AssetRepository repository = new InMemoryAssetRepository();
        AssetService sut = new AssetService(repository);
        assertThat(sut.getAssets()).isEqualTo(List.of());
    }
}
