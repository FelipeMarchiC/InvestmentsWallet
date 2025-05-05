package br.ifsp.demo.domain;

import br.ifsp.demo.exception.EntityAlreadyExistsException;
import br.ifsp.demo.security.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.LocalDate;
import java.util.*;

@Entity
@Getter
public class Wallet {
    @Id
    @JdbcTypeCode(Types.VARCHAR)
    private UUID id;
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Investment> investments;
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Investment> history;
    @OneToOne(mappedBy = "wallet")
    private User user;

    public Wallet() {
        id = UUID.randomUUID();
        investments = new LinkedHashSet<>();
        history = new LinkedHashSet<>();
    }

    public void addInvestment(Investment investment) {
        Objects.requireNonNull(investment, "Investment cannot be null");
        if (investments.contains(investment))
            throw new EntityAlreadyExistsException("Investment already exists in the wallet: " + investment.getId());
        investments.add(investment);
    }

    public void removeInvestment(Investment investment) {
        Objects.requireNonNull(investment, "Investment cannot be null");
        investments.remove(investment);
    }

    public void addInvestmentOnHistory(Investment investment) {
        Objects.requireNonNull(investment, "Investment cannot be null");
        if (!history.add(investment)) throw new EntityAlreadyExistsException("Investment already exists in the wallet: " + investment.getId());
    }

    public void undoAddInvestmentOnHistory(Investment investment) {
        Objects.requireNonNull(investment, "Investment cannot be null");
        history.remove(investment);
    }

    public double getTotalBalance(LocalDate withdrawDate) {
        double total = 0.0;
        for (Investment investment : history){
            total += investment.calculateBalanceAt(null);
        }
        for (Investment investment : investments) {
            total += investment.calculateBalanceAt(withdrawDate);
        }
        return total;
    }

    public double getFutureBalance() {
        double futureBalance = 0.0;
        for (Investment investment : investments) {
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

    public List<Investment> getInvestments() {
        return new ArrayList<>(investments);
    }

    public List<Investment> getHistoryInvestments() {
        return new ArrayList<>(history);
    }

    public Optional<Investment> getInvestmentById(UUID investmentId) {
        Objects.requireNonNull(investmentId, "Investment id cannot be null");
        return investments.stream()
                .filter(investment -> investment.getId().equals(investmentId))
                .findFirst();
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
