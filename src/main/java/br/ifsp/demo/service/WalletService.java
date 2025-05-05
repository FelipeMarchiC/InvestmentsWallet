package br.ifsp.demo.service;

import br.ifsp.demo.domain.AssetType;
import br.ifsp.demo.domain.Investment;
import br.ifsp.demo.domain.Wallet;
import br.ifsp.demo.repository.WalletRepository;
import br.ifsp.demo.security.user.JpaUserRepository;
import br.ifsp.demo.security.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class WalletService {
    private final WalletRepository repository;
    private final JpaUserRepository jpaUserRepository;

    public WalletService(WalletRepository repository, JpaUserRepository jpaUserRepository) {
        this.repository = repository;
        this.jpaUserRepository = jpaUserRepository;
    }

    public Wallet createWallet(UUID userId) {
        Objects.requireNonNull(userId, "User id cannot be null");
        User user = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
        Wallet wallet = new Wallet();
        user.setWallet(wallet);
        repository.save(wallet);
        return wallet;
    }

    public void addInvestment(UUID walletId, Investment investment) {
        Objects.requireNonNull(walletId, "Wallet id cannot be null");
        Wallet wallet = repository.findById(walletId)
                .orElseThrow(() -> new NoSuchElementException("Wallet not found: " + walletId));
        wallet.addInvestment(investment);
        repository.save(wallet);
    }

    public boolean withdrawInvestment(UUID walletId, UUID investmentId, LocalDate withdrawDate) {
        Objects.requireNonNull(walletId, "Wallet id cannot be null");
        Objects.requireNonNull(withdrawDate, "withdrawDate cannot be null");
        Wallet wallet = repository.findById(walletId)
                .orElseThrow(() -> new NoSuchElementException("Wallet not found: " + walletId));

        Investment investment = wallet.getInvestmentById(investmentId)
                .orElseThrow(() -> new NoSuchElementException("Investment not found: " + investmentId));

        wallet.addInvestmentOnHistory(investment);

        try {
            investment.setWithdrawDate(withdrawDate);
            wallet.removeInvestment(investment);
            repository.save(wallet);
            return true;
        } catch (Exception e) {
            wallet.undoAddInvestmentOnHistory(investment);
            investment.setWithdrawDate(null);
            wallet.addInvestment(investment);

            throw new IllegalStateException("Failed to execute withdrawInvestment: rollback applied", e);
        }
    }

    public List<Investment> getInvestments(UUID walletId) {
        Objects.requireNonNull(walletId, "Wallet id cannot be null");
        Optional<Wallet> walletOptional = repository.findById(walletId);
        if (walletOptional.isEmpty()) throw new NoSuchElementException("Wallet not found: " + walletId);

        Wallet wallet = walletOptional.get();
        return wallet.getInvestments();
    }

    public List<Investment> getHistoryInvestments(UUID walletId) {
        Objects.requireNonNull(walletId, "Wallet id cannot be null");
        Optional<Wallet> walletOptional = repository.findById(walletId);
        if (walletOptional.isEmpty()) throw new NoSuchElementException("Wallet not found: " + walletId);

        Wallet wallet = walletOptional.get();
        return wallet.getHistoryInvestments();
    }

    public List<Investment> filterHistory(UUID walletId, AssetType assetType) {
        Objects.requireNonNull(walletId, "Wallet id cannot be null");
        Objects.requireNonNull(assetType, "assetType cannot be null");
        return getHistoryInvestments(walletId).stream()
                .filter(investment -> investment.getAsset().getAssetType() == assetType)
                .toList();
    }

    public List<Investment> filterHistory(UUID walletId, LocalDate initialDate, LocalDate finalDate) {
        Objects.requireNonNull(walletId, "Wallet id cannot be null");
        Objects.requireNonNull(initialDate, "initialDate cannot be null");
        Objects.requireNonNull(finalDate, "finalDate cannot be null");
        return getHistoryInvestments(walletId).stream()
                .filter(investment -> {
                    LocalDate d = investment.getPurchaseDate();
                    // d >= initialDate  &&  d <= finalDate
                    return !d.isBefore(initialDate) && !d.isAfter(finalDate);
                })
                .toList();
    }

    public List<Investment> filterActiveInvestments(UUID walletId, AssetType assetType) {
        Objects.requireNonNull(walletId, "Wallet id cannot be null");
        Objects.requireNonNull(assetType, "assetType cannot be null");
        return getInvestments(walletId).stream()
                .filter(investment -> investment.getAsset().getAssetType() == assetType)
                .toList();
    }

    public List<Investment> filterActiveInvestments(UUID walletId, LocalDate initialDate, LocalDate finalDate) {
        Objects.requireNonNull(walletId, "Wallet id cannot be null");
        Objects.requireNonNull(initialDate, "initialDate cannot be null");
        Objects.requireNonNull(finalDate, "finalDate cannot be null");
        return getInvestments(walletId).stream()
                .filter(investment -> {
                    LocalDate d = investment.getPurchaseDate();
                    // d >= initialDate  &&  d <= finalDate
                    return !d.isBefore(initialDate) && !d.isAfter(finalDate);
                })
                .toList();
    }

    public String generateReport(UUID walletId, LocalDate relativeDate) {
        Objects.requireNonNull(walletId, "Wallet id cannot be null");
        Wallet wallet = repository.findById(walletId)
                .orElseThrow(() -> new NoSuchElementException("Wallet not found: " + walletId));
        WalletReportService walletReportService = new WalletReportService(wallet);
        return walletReportService.generateReport(relativeDate);
    }
}