package br.ifsp.demo.dto.investment;

import java.time.LocalDate;
import java.util.UUID;

public record InvestmentResponseDTO(
        UUID id,
        Double initialValue,
        UUID assetId,
        LocalDate purchaseDate,
        LocalDate withdrawDate,
        UUID walletId
) {
}