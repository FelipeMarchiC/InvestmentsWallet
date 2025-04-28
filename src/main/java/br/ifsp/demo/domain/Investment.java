package br.ifsp.demo.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Investment {
    private final double initialValue;
    private final Asset asset;
    private final UUID id;
    private final LocalDate purchaseDate;
    private LocalDate withdrawDate;

    public Investment(double initialValue, Asset asset) {
        this.purchaseDate = LocalDate.now();
        verifyInvestment(initialValue, asset, purchaseDate);
        this.initialValue = initialValue;
        this.asset = asset;
        this.id = UUID.randomUUID();
    }

    Investment(double initialValue, Asset asset, LocalDate purchaseDate) {
        verifyInvestment(initialValue, asset, purchaseDate);
        this.initialValue = initialValue;
        this.asset = asset;
        this.id = UUID.randomUUID();
        this.purchaseDate = purchaseDate;
    }

    public double getFutureBalance() {
        double totalBalance = 0.0;

        long days = ChronoUnit.DAYS.between(this.purchaseDate, this.asset.getMaturityDate());
        double time = days / 30.0;

        double profitability = this.asset.getProfitability();
        totalBalance += this.initialValue * Math.pow(1 + profitability, time);
        return Math.round(totalBalance * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return "Initial value = " + initialValue + " | " + asset.toString();
    }

    private void verifyInvestment(double initialValue, Asset asset, LocalDate purchaseDate){
        if (initialValue <= 0) throw new IllegalArgumentException("Initial value must be greater than zero");
        if (asset == null) throw new IllegalArgumentException("Asset cannot be null");
        if (purchaseDate == null) throw new IllegalArgumentException("Purchase date cannot be null");
        if (purchaseDate.isAfter(LocalDate.now())) throw new IllegalArgumentException("Purchase date cannot be in the future");
    }

    public UUID getId() {
        return this.id;
    }

    public double getInitialValue() {
        return initialValue;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public LocalDate getWithdrawDate() {
        return withdrawDate;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setWithdrawDate(LocalDate withdrawDate) {
        this.withdrawDate = withdrawDate;
    }
}
