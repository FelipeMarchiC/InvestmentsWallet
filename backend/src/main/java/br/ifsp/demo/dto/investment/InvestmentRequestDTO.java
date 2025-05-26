package br.ifsp.demo.dto.investment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record InvestmentRequestDTO(
        @NotNull(message = "The initial value is required")
        @Positive(message = "The initial value must be positive")
        Double initialValue,

        @NotNull(message = "The asset ID must not be null")
        UUID assetId
) {
}