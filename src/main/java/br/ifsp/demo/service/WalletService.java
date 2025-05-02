package br.ifsp.demo.service;

import br.ifsp.demo.domain.Asset;
import br.ifsp.demo.domain.AssetType;
import br.ifsp.demo.domain.Investment;
import br.ifsp.demo.domain.Wallet;
import br.ifsp.demo.repository.WalletRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static br.ifsp.demo.domain.AssetType.CDB;

public class WalletService {
    private final WalletRepository repository;

    public WalletService(WalletRepository repository) {
        this.repository = repository;
    }

    public boolean addInvestment(UUID walletId, Investment investment) {
        Optional<Wallet> walletOptional = repository.findById(walletId);
        if (walletOptional.isEmpty()) return false;

        Wallet wallet = walletOptional.get();
        wallet.addInvestment(investment);
        repository.save(wallet);
        return true;
    }

    public boolean withdrawInvestment(UUID walletId, UUID investmentId) {
        Objects.requireNonNull(walletId, "walletId cannot be null");
        Objects.requireNonNull(investmentId, "investmentId cannot be null");

        Wallet wallet = repository.findById(walletId)
                .orElseThrow(() -> new NoSuchElementException("Wallet not found: " + walletId));

        Investment investment = wallet.getInvestmentById(investmentId)
                .orElseThrow(() -> new NoSuchElementException("Investment not found: " + investmentId));

        boolean added = wallet.addInvestmentOnHistory(investment);
        if (!added) return false;

        investment.setWithdrawDate(LocalDate.now());
        wallet.removeInvestment(investment);
        repository.save(wallet);
        return true;
    }

    public List<Investment> getInvestments(UUID walletId) {
        Optional<Wallet> walletOptional = repository.findById(walletId);
        if (walletOptional.isEmpty()) return List.of();

        Wallet wallet = walletOptional.get();
        return wallet.getInvestments();
    }

    public List<Investment> getHistoryInvestments(UUID walletId) {
        Optional<Wallet> walletOptional = repository.findById(walletId);
        if (walletOptional.isEmpty()) return List.of();

        Wallet wallet = walletOptional.get();
        return wallet.getHistoryInvestments();
    }

    public List<Investment> filterHistory(UUID walletId, AssetType assetType) {
        return getHistoryInvestments(walletId).stream()
                .filter(investment -> investment.getAsset().getAssetType() == assetType)
                .toList();
    }

    public List<Investment> filterHistory(UUID walletId, LocalDate initialDate, LocalDate finalDate) {
        return getHistoryInvestments(walletId).stream()
                .filter(investment -> investment.getPurchaseDate().isAfter(initialDate)
                        && investment.getPurchaseDate().isBefore(finalDate) )
                .toList();
    }

    public List<Investment> filterActiveInvestments(UUID walletId, AssetType assetType) {
        return getInvestments(walletId).stream()
                .filter(investment -> investment.getAsset().getAssetType() == assetType)
                .toList();
    }

    public List<Investment> filterActiveInvestments(UUID walletId, LocalDate initialDate, LocalDate finalDate) {
        return getInvestments(walletId).stream()
                .filter(investment -> investment.getPurchaseDate().isAfter(initialDate)
                        && investment.getPurchaseDate().isBefore(finalDate) )
                .toList();
    }

    public String generateReport(UUID walletId) {
        Wallet wallet = repository.findById(walletId)
                .orElseThrow(() -> new NoSuchElementException("Wallet not found: " + walletId));
        return wallet.generateReport();
    }
}