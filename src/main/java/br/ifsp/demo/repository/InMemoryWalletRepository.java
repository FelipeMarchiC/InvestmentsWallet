package br.ifsp.demo.repository;

import br.ifsp.demo.domain.Wallet;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryWalletRepository implements WalletRepository {
    private final Map<UUID, Wallet> WalletContainer = new HashMap<>();

    @Override
    public Optional<Wallet> findById(UUID walletId) {
        return Optional.of(WalletContainer.get(walletId));
    }

    @Override
    public void save(Wallet wallet) {
        WalletContainer.put(wallet.getId(), wallet);
    }
}
