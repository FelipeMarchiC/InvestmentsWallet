package br.ifsp.demo.controller;

import br.ifsp.demo.domain.Asset;
import br.ifsp.demo.dto.asset.AssetResponseDTO;
import br.ifsp.demo.mapper.AssetMapper;
import br.ifsp.demo.repository.AssetRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/asset")
public class AssetController {
    AssetRepository repository;

    public AssetController(AssetRepository repository) {
        this.repository = repository;
    }

    @GetMapping()
    public ResponseEntity<List<AssetResponseDTO>> getAssets() {
        List<Asset> assets = repository.findAll();
        return ResponseEntity.ok().body(AssetMapper.listToResponseDTO(assets));
    }
}
