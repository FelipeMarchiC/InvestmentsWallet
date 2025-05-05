package br.ifsp.demo.dto.asset;

import br.ifsp.demo.domain.AssetType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.UUID;

public record AssetRequestDTO(
        @NotNull(message = "The asset ID must not be null")
        UUID id,

        @NotNull(message = "The asset name must not be null")
        String name,

        @NotNull(message = "The asset type must not be null")
        AssetType assetType,

        @Positive(message = "Profitability must be a positive number")
        double profitability,

        @NotNull(message = "The maturity date must not be null")
        @Future(message = "The maturity date must be in the future")
        LocalDate maturityDate
) {
}
