package br.ifsp.demo.repository;

import br.ifsp.demo.domain.Investment;

import java.util.ArrayList;
import java.util.List;

public class InMemoryWalletRepository implements WalletRepository {
    private final List<Investment> investments;

    public InMemoryWalletRepository() {
        this.investments = new ArrayList<>();
    }

    @Override
    public boolean addInvestment(Investment investment) {
        investments.add(investment);
        return true;
    }
}
