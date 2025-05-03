package br.ifsp.demo.service;

import br.ifsp.demo.domain.AssetType;
import br.ifsp.demo.domain.Investment;
import br.ifsp.demo.domain.Wallet;

import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;

public class WalletReportService {
    private final Wallet wallet;

    public WalletReportService(Wallet wallet) {
        this.wallet = wallet;
    }

    public String generateReport(LocalDate relativeDate) {
        if (wallet.getInvestments().isEmpty() && wallet.getHistoryInvestments().isEmpty()) {
            throw new NoSuchElementException("No investments found");
        }

        StringBuilder report = new StringBuilder();
        report.append("=========== WALLET REPORT ===========\n\n");

        if (!wallet.getInvestments().isEmpty()) {
            report.append("> Active Investments:\n");
            for (Investment investment : wallet.getInvestments()) {
                report.append("- ").append(investment.toString()).append("\n");
            }
            report.append("\n");
        }

        if (!wallet.getHistoryInvestments().isEmpty()) {
            report.append("> Historical Investments:\n");
            for (Investment investment : wallet.getHistoryInvestments()) {
                report.append("- ").append(investment.toString()).append("\n");
            }
            report.append("\n");
        }

        report.append("> Current Total Balance: R$ ").append(String.format("%.2f", wallet.getTotalBalance(relativeDate))).append("\n");
        report.append("> Future Investments Balance: R$ ").append(String.format("%.2f", wallet.getFutureBalance())).append("\n");
        report.append("> Total Balance (Current + Future): R$ ").append(String.format("%.2f", wallet.getTotalBalance(relativeDate) + wallet.getFutureBalance())).append("\n\n");
        report.append("> Investment by Type: \n").append(generateAndFormatInvestmentsByType());
        return report.toString();
    }

    private String generateAndFormatInvestmentsByType() {
        StringBuilder investmentsByType = new StringBuilder();
        Map<AssetType, Double> activeInvestmentsPerTypePercentage = wallet.filterInvestmentsByTypeAndPercentage(wallet.getInvestments());
        Map<AssetType, Double> historicalPerTypePercentage = wallet.filterInvestmentsByTypeAndPercentage(wallet.getHistoryInvestments());

        investmentsByType.append("- Active investments by type: ").append("\n");
        formatInvestmentTypePercentages(investmentsByType, activeInvestmentsPerTypePercentage);
        investmentsByType.append("\n");
        investmentsByType.append("- Historical investments by type: ").append("\n");
        formatInvestmentTypePercentages(investmentsByType, historicalPerTypePercentage);
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
