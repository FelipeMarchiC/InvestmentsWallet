package br.ifsp.demo.repository;

import br.ifsp.demo.domain.Wallet;

import java.util.*;

public class InMemoryWalletRepository implements WalletRepository {
    private final Map<UUID, Wallet> walletContainer;

    public InMemoryWalletRepository() {
        this.walletContainer = new HashMap<>();
    }

    @Override
    public Optional<Wallet> findById(UUID walletId) {
        Objects.requireNonNull(walletId, "Wallet id cannot be null");
        return Optional.ofNullable(walletContainer.get(walletId));
    }


    @Override
    public void save(Wallet wallet) {
        Objects.requireNonNull(wallet, "Wallet cannot be null");
        walletContainer.put(wallet.getId(), wallet);
    }
}
