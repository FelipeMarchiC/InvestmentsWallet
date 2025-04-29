package br.ifsp.demo.repository;

import br.ifsp.demo.domain.Asset;

import java.util.List;

public class InMemoryAssetRepository implements AssetRepository {
    @Override
    public List<Asset> getAssets() {
        return List.of();
    }
}
