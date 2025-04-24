package br.ifsp.demo.service;

import br.ifsp.demo.domain.Investment;
import br.ifsp.demo.repository.WalletRepository;

public class WalletService {
    private final WalletRepository repository;

    public WalletService(WalletRepository repository) {
        this.repository = repository;
    }

    public boolean addInvestment(Investment investment) {
        return repository.addInvestment(investment);
    }
}
