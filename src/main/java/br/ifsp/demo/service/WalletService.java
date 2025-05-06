package br.ifsp.demo.service;

import br.ifsp.demo.domain.AssetType;
import br.ifsp.demo.domain.Investment;
import br.ifsp.demo.domain.Wallet;
import br.ifsp.demo.repository.WalletRepository;
import br.ifsp.demo.security.user.JpaUserRepository;
import br.ifsp.demo.security.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

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

        if (user.getWallet() != null) {
            throw new IllegalStateException("User already has a wallet: " + userId);
        }

        Wallet wallet = new Wallet();
        wallet.setUser(user);
//        user.setWallet(wallet);
        return repository.save(wallet);
    }

    public void addInvestment(UUID userId, Investment investment) {
        Objects.requireNonNull(userId, "User id cannot be null");
        Objects.requireNonNull(investment, "Investment cannot be null");

        Wallet wallet = repository.findByUser_Id(userId)
                .orElseThrow(() -> new NoSuchElementException("This user has not a wallet: " + userId));

        wallet.addInvestment(investment);
        repository.save(wallet);
    }

    public void removeInvestment(UUID userId, UUID investmentId) {
        Objects.requireNonNull(userId, "User id cannot be null");
        Objects.requireNonNull(investmentId, "Investment id cannot be null");

        Wallet wallet = repository.findByUser_Id(userId)
                .orElseThrow(() -> new NoSuchElementException("This user has not a wallet: " + userId));

        Investment investment = wallet.getInvestmentById(investmentId)
                .orElseThrow(() -> new NoSuchElementException("Investment not found: " + investmentId));

        wallet.removeInvestment(investment);
        repository.save(wallet);
    }

    public void withdrawInvestment(UUID userId, UUID investmentId, LocalDate withdrawDate) {
        Objects.requireNonNull(userId, "User id cannot be null");
        Objects.requireNonNull(investmentId, "Investment id cannot be null");
        Objects.requireNonNull(withdrawDate, "withdrawDate cannot be null");

        Wallet wallet = repository.findByUser_Id(userId)
                .orElseThrow(() -> new NoSuchElementException("This user has not a wallet: " + userId));

        Investment investment = wallet.getInvestmentById(investmentId)
                .orElseThrow(() -> new NoSuchElementException("Investment not found: " + investmentId));

        investment.setWithdrawDate(withdrawDate);
        repository.save(wallet);
    }

    public List<Investment> getInvestments(UUID userId) {
        Objects.requireNonNull(userId, "User id cannot be null");
        Wallet wallet = repository.findByUser_Id(userId)
                .orElseThrow(() -> new NoSuchElementException("This user has not a wallet: " + userId));

        return wallet.getInvestments();
    }

    public List<Investment> getHistoryInvestments(UUID userId) {
        Objects.requireNonNull(userId, "User id cannot be null");
        Wallet wallet = repository.findByUser_Id(userId)
                .orElseThrow(() -> new NoSuchElementException("This user has not a wallet: " + userId));

        return wallet.getHistoryInvestments();
    }

    public List<Investment> filterHistory(UUID userId, AssetType assetType) {
        Objects.requireNonNull(userId, "User id cannot be null");
        Objects.requireNonNull(assetType, "AssetType cannot be null");

        return getHistoryInvestments(userId).stream()
                .filter(investment -> investment.getAsset().getAssetType() == assetType)
                .toList();
    }

    public List<Investment> filterHistory(UUID userId, LocalDate initialDate, LocalDate finalDate) {
        Objects.requireNonNull(userId, "User id cannot be null");
        Objects.requireNonNull(initialDate, "initialDate cannot be null");
        Objects.requireNonNull(finalDate, "finalDate cannot be null");

        return getHistoryInvestments(userId).stream()
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

        return getInvestments(userId).stream()
                .filter(investment -> investment.getAsset().getAssetType() == assetType)
                .toList();
    }

    public List<Investment> filterActiveInvestments(UUID userId, LocalDate initialDate, LocalDate finalDate) {
        Objects.requireNonNull(userId, "User id cannot be null");
        Objects.requireNonNull(initialDate, "initialDate cannot be null");
        Objects.requireNonNull(finalDate, "finalDate cannot be null");

        return getInvestments(userId).stream()
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

        Wallet wallet = repository.findByUser_Id(userId)
                .orElseThrow(() -> new NoSuchElementException("This user has not a wallet: " + userId));

        WalletReportService walletReportService = new WalletReportService(wallet);
        return walletReportService.generateReport(relativeDate);
    }

    public Wallet getWallet(UUID userId) {
        Objects.requireNonNull(userId, "User id cannot be null");

        return repository.findByUser_Id(userId)
                .orElseThrow(() -> new NoSuchElementException("This user has not a wallet: " + userId));
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

        Wallet wallet = getWallet(userId);
        return wallet.getTotalBalance(withdrawDate);
    }

    public double getFutureBalance(UUID userId) {
        Objects.requireNonNull(userId, "User id cannot be null");

        Wallet wallet = getWallet(userId);
        return wallet.getFutureBalance();
    }
}
