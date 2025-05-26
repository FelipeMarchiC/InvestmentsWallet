package br.ifsp.demo.dto.wallet;

import br.ifsp.demo.dto.investment.InvestmentResponseDTO;

import java.util.List;
import java.util.UUID;

public record WalletResponseDTO(
        UUID id,
        List<InvestmentResponseDTO> investments,
        List<InvestmentResponseDTO> history) {
}
