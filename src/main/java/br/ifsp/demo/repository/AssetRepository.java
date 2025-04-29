package br.ifsp.demo.repository;

import br.ifsp.demo.domain.Asset;

import java.util.List;

public interface AssetRepository {
    List<Asset> getAssets();
}
