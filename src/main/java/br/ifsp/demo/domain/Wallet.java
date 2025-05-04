package br.ifsp.demo.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class Wallet {
    private final UUID id;
    private final Map<UUID, Investment> investments;
    private final Map<UUID, Investment> history;

    public Wallet() {
        id = UUID.randomUUID();
        investments = new LinkedHashMap<>();
        history = new LinkedHashMap<>();
    }

    public void addInvestment(Investment investment) {
        Objects.requireNonNull(investment, "Investment cannot be null");
        if (investments.containsKey(investment.getId()))
            throw new IllegalArgumentException("Investment already exists in the wallet: " + investment.getId());
        investments.put(investment.getId(), investment);
    }

    public void removeInvestment(Investment investment) {
        investments.remove(investment.getId());
    }

    public void addInvestmentOnHistory(Investment investment) {
        Optional<Investment> added = Optional.ofNullable(history.put(investment.getId(), investment));
        if (added.isPresent()) throw new IllegalArgumentException("Could not move investment to history");
    }

    public void undoAddInvestmentOnHistory(Investment investment) {
        history.remove(investment.getId());
    }

    public double getTotalBalance(LocalDate withdrawDate) {
        double total = 0.0;
        for (Investment investment : history.values()) {
            total += investment.calculateBalanceAt(null);
        }
        for (Investment investment : investments.values()) {
            total += investment.calculateBalanceAt(withdrawDate);
        }
        return total;
    }

    public double getFutureBalance() {
        double futureBalance = 0.0;
        for (Investment investment : investments.values()) {
            futureBalance += investment.calculateBalanceAt(investment.getMaturityDate());
        }
        return futureBalance;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Wallet wallet = (Wallet) o;
        return Objects.equals(id, wallet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public UUID getId() {
        return id;
    }

    public List<Investment> getInvestments() {
        return new ArrayList<>(investments.values());
    }

    public List<Investment> getHistoryInvestments() {
        return new ArrayList<>(history.values());
    }

    public Optional<Investment> getInvestmentById(UUID investmentId) {
        Objects.requireNonNull(investmentId, "Investment id cannot be null");
        return Optional.ofNullable(investments.get(investmentId));
    }

    public Map<AssetType, Double> filterInvestmentsByTypeAndPercentage(List<Investment> investmentStorage) {
        Objects.requireNonNull(investmentStorage, "Investment storage cannot be null");
        double totalStorage = investmentStorage.size();
        if (totalStorage == 0) return Collections.emptyMap();

        Map<AssetType, Double> typesCount = new LinkedHashMap<>();
        AssetType[] types = AssetType.values();
        Arrays.stream(types).forEach(type -> {
            long totalByType = investmentStorage.stream().filter(investment -> investment.getAsset().getAssetType() == type).count();
            Double percentage = ((totalByType / totalStorage) * 100.0);
            typesCount.put(type, percentage);
        });
        return Collections.unmodifiableMap(typesCount);
    }
}
