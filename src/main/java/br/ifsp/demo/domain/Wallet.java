package br.ifsp.demo.domain;

import br.ifsp.demo.exception.EntityAlreadyExistsException;
import br.ifsp.demo.security.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
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
    @Setter
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "fk_wallet_user"))
    private User user;

    public Wallet() {
        id = UUID.randomUUID();
        investments = new LinkedHashSet<>();
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

    public double getTotalBalance() {
        double total = 0.0;
        for (Investment investment : getHistoryInvestments()){
            total += investment.calculateCurrentBalance();
        }
        for (Investment investment : getInvestments()) {
            total += investment.calculateCurrentBalance();
        }
        return total;
    }

    public double getFutureBalance() {
        double futureBalance = 0.0;
        for (Investment investment : getInvestments()) {
            futureBalance += investment.calculateFutureBalance();
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
        return investments.stream().filter(investment -> !investment.isWithdrawn()).toList();
    }

    public List<Investment> getHistoryInvestments() {
        return investments.stream().filter(Investment::isWithdrawn).toList();
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
