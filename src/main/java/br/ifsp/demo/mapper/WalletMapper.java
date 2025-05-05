package br.ifsp.demo.mapper;

import br.ifsp.demo.domain.Investment;
import br.ifsp.demo.domain.Wallet;
import br.ifsp.demo.dto.investment.InvestmentResponseDTO;
import br.ifsp.demo.dto.wallet.WalletResponseDTO;

import java.util.List;

public class WalletMapper {
    public static WalletResponseDTO toResponseDTO(Wallet wallet, List<Investment> investments, List<Investment> history) {
        List<InvestmentResponseDTO> investmentResponseDTOS = InvestmentMapper.listToResponseDTO(investments, wallet);
        List<InvestmentResponseDTO> historyResponseDTOS = InvestmentMapper.listToResponseDTO(history, wallet);

        return new WalletResponseDTO(wallet.getId(), investmentResponseDTOS, historyResponseDTOS);
    }
}
