package br.ifsp.demo.service;

import br.ifsp.demo.domain.AssetType;
import br.ifsp.demo.domain.Investment;
import br.ifsp.demo.domain.Wallet;
import br.ifsp.demo.repository.WalletRepository;
import br.ifsp.demo.security.user.JpaUserRepository;
import br.ifsp.demo.security.user.User;
import jakarta.transaction.Transactional;
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

        if (user.getWallet() != null) throw new IllegalStateException("User already has a wallet");

        Wallet wallet = new Wallet();
        user.setWallet(wallet);
        jpaUserRepository.save(user);
        return user.getWallet();
    }

    public void addInvestment(UUID userId, Investment investment) {
        Objects.requireNonNull(userId, "User id cannot be null");
        Objects.requireNonNull(investment, "Investment cannot be null");

        User user = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        user.getWallet().addInvestment(investment);
        jpaUserRepository.save(user);
    }

    public void removeInvestment(UUID userId, UUID investmentId) {
        Objects.requireNonNull(userId, "User id cannot be null");
        Objects.requireNonNull(investmentId, "Investment id cannot be null");

        User user = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        Wallet wallet = getWallet(userId);
        Investment investment = wallet.getInvestmentById(investmentId)
                .orElseThrow(() -> new NoSuchElementException("Investment not found: " + investmentId));

        wallet.removeInvestment(investment);
        jpaUserRepository.save(user);
    }

    @Transactional
    public void withdrawInvestment(UUID userId, UUID investmentId, LocalDate withdrawDate) {
        Objects.requireNonNull(userId, "User id cannot be null");
        Objects.requireNonNull(investmentId, "Investment id cannot be null");
        Objects.requireNonNull(withdrawDate, "Withdraw date cannot be null");

        User user = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        Wallet wallet = getWallet(userId);
        Investment investment = wallet.getInvestmentById(investmentId)
                .orElseThrow(() -> new NoSuchElementException("Investment not found: " + investmentId));

        wallet.addInvestmentOnHistory(investment);
        investment.setWithdrawDate(withdrawDate);
        wallet.removeInvestment(investment);

        jpaUserRepository.save(user);
    }

    public List<Investment> getInvestments(UUID userId) {
        Objects.requireNonNull(userId, "User id cannot be null");
        User user = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        Wallet wallet = getWallet(userId);
        return wallet.getInvestments();
    }

    public List<Investment> getHistoryInvestments(UUID userId) {
        Objects.requireNonNull(userId, "User id cannot be null");
        User user = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        Wallet wallet = getWallet(userId);
        return wallet.getHistoryInvestments();
    }

    public List<Investment> filterHistory(UUID userId, AssetType assetType) {
        Objects.requireNonNull(userId, "User id cannot be null");
        Objects.requireNonNull(assetType, "AssetType cannot be null");

        User user = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
        Wallet wallet = user.getWallet();

        return getHistoryInvestments(wallet.getId()).stream()
                .filter(investment -> investment.getAsset().getAssetType() == assetType)
                .toList();
    }

    public List<Investment> filterHistory(UUID userId, LocalDate initialDate, LocalDate finalDate) {
        Objects.requireNonNull(userId, "User id cannot be null");
        Objects.requireNonNull(initialDate, "initialDate cannot be null");
        Objects.requireNonNull(finalDate, "finalDate cannot be null");

        User user = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
        Wallet wallet = user.getWallet();

        return getHistoryInvestments(wallet.getId()).stream()
                .filter(investment -> {
                    LocalDate d = investment.getPurchaseDate();
                    // d >= initialDate  &&  d <= finalDate
                    return !d.isBefore(initialDate) && !d.isAfter(finalDate);
                })
                .toList();
    }

    public List<Investment> filterActiveInvestments(UUID userId, AssetType assetType) {
        Objects.requireNonNull(userId, "User id cannot be null");
        Objects.requireNonNull(assetType, "assetType cannot be null");

        User user = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
        Wallet wallet = user.getWallet();

        return getInvestments(wallet.getId()).stream()
                .filter(investment -> investment.getAsset().getAssetType() == assetType)
                .toList();
    }

    public List<Investment> filterActiveInvestments(UUID userId, LocalDate initialDate, LocalDate finalDate) {
        Objects.requireNonNull(userId, "User id cannot be null");
        Objects.requireNonNull(initialDate, "initialDate cannot be null");
        Objects.requireNonNull(finalDate, "finalDate cannot be null");

        User user = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
        Wallet wallet = user.getWallet();

        return getInvestments(wallet.getId()).stream()
                .filter(investment -> {
                    LocalDate d = investment.getPurchaseDate();
                    // d >= initialDate  &&  d <= finalDate
                    return !d.isBefore(initialDate) && !d.isAfter(finalDate);
                })
                .toList();
    }

    public String generateReport(UUID userId, LocalDate relativeDate) {
        Objects.requireNonNull(userId, "User id cannot be null");
        Objects.requireNonNull(relativeDate, "Relative date cannot be null");

        User user = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        Wallet wallet = getWallet(userId);
        WalletReportService walletReportService = new WalletReportService(wallet);
        return walletReportService.generateReport(relativeDate);
    }

    public Wallet getWallet(UUID userId) {
        Objects.requireNonNull(userId, "User id cannot be null");

        User user = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        if (user.getWallet() == null) throw new IllegalStateException("User does not have an associated wallet");
        return user.getWallet();
    }

    public Investment getInvestmentById(UUID userId, UUID investmentId) {
        Objects.requireNonNull(userId, "User id cannot be null");
        Objects.requireNonNull(investmentId, "Investment id cannot be null");

        Wallet wallet = getWallet(userId);
        return wallet.getInvestmentById(investmentId)
                .orElseThrow(() -> new NoSuchElementException("Investment not found: " + investmentId));
    }

    public double getTotalBalance(UUID userId, LocalDate withdrawDate) {
        Objects.requireNonNull(userId, "User id cannot be null");
        Objects.requireNonNull(withdrawDate, "withdrawDate cannot be null");

        Wallet wallet = getWallet(userId);
        return wallet.getTotalBalance(withdrawDate);
    }

    public double getFutureBalance(UUID userId) {
        Objects.requireNonNull(userId, "User id cannot be null");

        Wallet wallet = getWallet(userId);
        return wallet.getFutureBalance();
    }
}
