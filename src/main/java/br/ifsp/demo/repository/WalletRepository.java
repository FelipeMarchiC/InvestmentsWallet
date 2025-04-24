package br.ifsp.demo.repository;

import br.ifsp.demo.domain.Investment;

public interface WalletRepository {
    boolean addInvestment(Investment investment);
}
