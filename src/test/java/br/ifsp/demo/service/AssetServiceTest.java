package br.ifsp.demo.service;

import br.ifsp.demo.repository.AssetRepository;
import br.ifsp.demo.repository.InMemoryAssetRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


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

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Should return an empty list if there is no assets")
    void shouldReturnAnEmptyListIfThereIsNoAssets(){
        AssetService sut = mock();

        when(sut.getAssets()).thenReturn(List.of());

        assertThat(sut.getAssets()).isEqualTo(List.of());
    }
}
