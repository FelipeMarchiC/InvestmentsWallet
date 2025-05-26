package br.ifsp.demo.dto.asset;

import br.ifsp.demo.domain.AssetType;

import java.time.LocalDate;
import java.util.UUID;

public record AssetResponseDTO(
        UUID id,
        String name,
        AssetType assetType,
        double profitability,
        LocalDate maturityDate
) {
}