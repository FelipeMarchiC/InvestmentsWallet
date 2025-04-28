package br.ifsp.demo.domain;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Wallet {
    private final UUID id;
    private final Map<UUID, Investment> investments;
    private final Map<UUID, Investment> history;

    public Wallet() {
        id = UUID.randomUUID();
        investments = new HashMap<>();
        history = new HashMap<>();
    }

    public void addInvestment(Investment investment) {
        investments.put(investment.getId(), investment);
    }

    public void removeInvestment(Investment investment) {
        investments.remove(investment.getId());
    }

    public boolean addInvestmentOnHistory(Investment investment) {
        Optional<Investment> added = Optional.ofNullable(history.put(investment.getId(), investment));
        return added.isEmpty();
    }

    private double calculateHistoryBalance() {
        return calculateInvestmentsBalance(history);
    }

    private double calculateActiveInvestmentsBalance() {
        return calculateInvestmentsBalance(investments);
    }

    private double calculateInvestmentsBalance(Map<UUID, Investment> investmentStorage) {
        double totalBalance = 0.0;
        for (Investment investment : investmentStorage.values()) {
            LocalDate withdrawDate = investment.getWithdrawDate();
            LocalDate effectiveWithdrawDate = withdrawDate != null ? withdrawDate : LocalDate.now();

            long days = ChronoUnit.DAYS.between(investment.getPurchaseDate(), effectiveWithdrawDate);
            double time = days / 30.0;

            double initialValue = investment.getInitialValue();
            double profitability = investment.getAsset().getProfitability();
            totalBalance += initialValue * Math.pow(1 + profitability, time);
        }
        return Math.round(totalBalance * 100.0) / 100.0;
    }

    public double getTotalBalance() {
        return calculateHistoryBalance() + calculateActiveInvestmentsBalance();
    }

    public UUID getId() {
        return id;
    }

    public List<Investment> getInvestments() {
        return new ArrayList<>(investments.values());
    }

    public Optional<Investment> getInvestmentById(UUID investmentId) {
        return Optional.ofNullable(investments.get(investmentId));
    }
}
