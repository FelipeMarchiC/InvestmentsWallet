package br.ifsp.demo.service;

import br.ifsp.demo.domain.Investment;
import br.ifsp.demo.domain.Wallet;
import br.ifsp.demo.repository.WalletRepository;

import java.util.Optional;
import java.util.UUID;

public class WalletService {
    private final WalletRepository repository;

    public WalletService(WalletRepository repository) {
        this.repository = repository;
    }

    public boolean addInvestment(UUID walletId, Investment investment) {
        Optional<Wallet> walletOptional = repository.findById(walletId);
        if (walletOptional.isEmpty()) return false;

        Wallet wallet = walletOptional.get();
        wallet.addInvestiment(investment);
        repository.save(wallet);
        return true;
    }

    public boolean withdrawInvestment(UUID walletId, UUID investmentId) {
        return true;
    }
}