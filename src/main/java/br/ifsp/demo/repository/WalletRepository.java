package br.ifsp.demo.repository;

import br.ifsp.demo.domain.Wallet;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository {
    Optional<Wallet> findById(UUID walletId);
    void save(Wallet wallet);
}
