package br.ifsp.demo.service;

import br.ifsp.demo.domain.Asset;
import br.ifsp.demo.repository.AssetRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
public class AssetService {
    AssetRepository repository;

    public AssetService(AssetRepository repository) {
        this.repository = repository;
    }

    public Asset getAssetById(UUID assetId) {
        Objects.requireNonNull(assetId, "assetId cannot be null");
        return repository.findById(assetId)
                .orElseThrow(() -> new NoSuchElementException("Asset not found for id: " + assetId));
    }
}
