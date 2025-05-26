package br.ifsp.demo.mapper;

import br.ifsp.demo.domain.Investment;
import br.ifsp.demo.domain.Wallet;
import br.ifsp.demo.dto.investment.InvestmentResponseDTO;

import java.util.List;

public class InvestmentMapper {
    public static List<InvestmentResponseDTO> listToResponseDTO(List<Investment> investments, Wallet wallet) {
        return investments.stream()
                .map(investment -> new InvestmentResponseDTO(
                        investment.getId(),
                        investment.getInitialValue(),
                        investment.getAsset().getId(),
                        investment.getPurchaseDate(),
                        investment.getWithdrawDate(),
                        wallet.getId()))
                .toList();
    }

    public static InvestmentResponseDTO toResponseDTO(Investment investment) {
        return new InvestmentResponseDTO(
                investment.getId(),
                investment.getInitialValue(),
                investment.getAsset().getId(),
                investment.getPurchaseDate(),
                investment.getWithdrawDate(),
                investment.getWallet().getId());
    }
}
