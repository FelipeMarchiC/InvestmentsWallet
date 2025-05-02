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
        return Optional.ofNullable(investments.get(investmentId));
    }

    private Map<AssetType, Double> getInvestmentsByTypeAndPercentage(Map<UUID, Investment> investmentStorage) {
        double totalStorage = investmentStorage.size();
        Map<AssetType, Double> typesCount = new HashMap<>();
        AssetType[] types = AssetType.values();
        Arrays.stream(types).toList().forEach(type -> {
            long totalByType = investmentStorage.values().stream().filter(investment -> investment.getAsset().getAssetType() == type).count();
            Double percentage = ((totalByType / totalStorage) * 100.0);
            typesCount.put(type, percentage);
        });
        return typesCount;
    }

     public String generateReport(LocalDate relativeDate) {
        if (investments.isEmpty() && history.isEmpty()) throw new NoSuchElementException("No investments found");
        StringBuilder report = new StringBuilder();

        report.append("=========== WALLET REPORT ===========\n\n");

        if (!investments.isEmpty()) {
            report.append("> Active Investments:\n");
            for (Investment investment : investments.values()) {
                report.append("- ")
                        .append(investment.toString())
                        .append("\n");
            }
            report.append("\n");
        }

        if (!history.isEmpty()) {
            report.append("> Historical Investments:\n");
            for (Investment investment : history.values()) {
                report.append("- ")
                        .append(investment.toString())
                        .append("\n");
            }
            report.append("\n");
        }

        report.append("> Current Total Balance: R$ ")
                .append(String.format("%.2f", getTotalBalance(relativeDate))).append("\n");
        report.append("> Future Investments Balance: R$ ")
                .append(String.format("%.2f", getFutureBalance())).append("\n");
        report.append("> Total Balance (Current + Future): R$ ")
                .append(String.format("%.2f", getTotalBalance(relativeDate) + getFutureBalance())).append("\n\n");
        report.append("> Investment by Type: \n").append(generateAndFormatInvestmentsByType());
        return report.toString();
    }

    private String generateAndFormatInvestmentsByType() {
        StringBuilder investmentsByType = new StringBuilder();
        Map<AssetType, Double> countActive = getInvestmentsByTypeAndPercentage(investments);
        Map<AssetType, Double> countHistory = getInvestmentsByTypeAndPercentage(history);

        investmentsByType.append("- Active investments by type: ").append("\n");
        formatInvestmentTypePercentages(investmentsByType, countActive);
        investmentsByType.append("\n");
        investmentsByType.append("- Historical investments by type: ").append("\n");
        formatInvestmentTypePercentages(investmentsByType, countHistory);
        return investmentsByType.toString();
    }

    private void formatInvestmentTypePercentages(StringBuilder investmentsByType, Map<AssetType, Double> countStorage) {
        for (Map.Entry<AssetType, Double> entry : countStorage.entrySet()) {
            AssetType type = entry.getKey();
            Double percentage = entry.getValue();
            investmentsByType.append(" | ");
            investmentsByType.append(type.toString());
            investmentsByType.append(": ");
            investmentsByType.append(String.format("%.2f%%", percentage));
        }
    }
}
