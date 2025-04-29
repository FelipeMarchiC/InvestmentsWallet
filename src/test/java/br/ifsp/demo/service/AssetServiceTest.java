package br.ifsp.demo.service;

import br.ifsp.demo.repository.AssetRepository;
import br.ifsp.demo.repository.InMemoryAssetRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class AssetServiceTest {
    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Should return the registered assets")
    void shouldReturnTheRegisteredAssets(){
        AssetRepository repository = new InMemoryAssetRepository();
        AssetService sut = new AssetService(repository);
        assertThat(sut.getAssets().size()).isGreaterThan(0);
    }
}
