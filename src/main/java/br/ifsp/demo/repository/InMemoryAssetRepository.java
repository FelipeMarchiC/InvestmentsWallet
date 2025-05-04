package br.ifsp.demo.repository;

import br.ifsp.demo.domain.Asset;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static br.ifsp.demo.domain.AssetType.CDB;

public class InMemoryAssetRepository implements AssetRepository {
    private final Map<UUID, Asset> assetContainer;

    public InMemoryAssetRepository() {
        this.assetContainer = new HashMap<>();
        setUpInitialAssets();
    }

    private void setUpInitialAssets() {
        assetContainer.put(
                UUID.randomUUID(),
                new Asset("Banco Inter", CDB, 0.01, LocalDate.now().plusYears(1))
        );
    }

    @Override
    public List<Asset> getAssets() {
        return assetContainer.values().stream().toList();
    }
}
