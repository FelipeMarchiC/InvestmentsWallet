package br.ifsp.demo.domain;

import java.time.LocalDate;

public class Asset {
    private final String name;
    private final double profitability;
    private final LocalDate maturityDate;

    public Asset(String name, double profitability, LocalDate maturityDate) {
        verifyAsset(name, profitability, maturityDate);
        this.name = name;
        this.profitability = profitability;
        this.maturityDate = maturityDate;
    }

    private void verifyAsset(String name, double profitability, LocalDate maturityDate){
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Asset name cannot be null or blank");
        if (profitability <= 0) throw new IllegalArgumentException("Asset profitability must be greater than zero");
        if (maturityDate == null) throw new IllegalArgumentException("Asset maturity date cannot be null");
        if (maturityDate.isBefore(LocalDate.now())) throw new IllegalArgumentException("Asset maturity date cannot be in the past");
    }

    @Override
    public String toString() {
        return "Asset name = " + name + " | Asset profitability = " + profitability + " | Asset maturity date = " + maturityDate;
    }

    public String getName() {
        return name;
    }

    public double getProfitability() {
        return profitability;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }
}
