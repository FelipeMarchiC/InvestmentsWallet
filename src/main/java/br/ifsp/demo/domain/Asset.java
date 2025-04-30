package br.ifsp.demo.domain;

import br.ifsp.demo.util.DateFormatter;

import java.time.LocalDate;

public class Asset {
    private final String name;
    private final AssetType assetType;
    private final double profitability;
    private final LocalDate maturityDate;

    public Asset(String name, AssetType assetType, double profitability, LocalDate maturityDate) {
        verifyAsset(name, assetType, profitability, maturityDate);
        this.name = name;
        this.assetType = assetType;
        this.profitability = profitability;
        this.maturityDate = maturityDate;
    }

    private void verifyAsset(String name, AssetType assetType, double profitability, LocalDate maturityDate){
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Asset name cannot be null or blank");
        if (assetType == null) throw new IllegalArgumentException("Asset type cannot be null");
        if (profitability <= 0) throw new IllegalArgumentException("Asset profitability must be greater than zero");
        if (profitability < 0.01) throw new IllegalArgumentException("Asset profitability must be greater or equal 0.01");
        if (maturityDate == null) throw new IllegalArgumentException("Asset maturity date cannot be null");
        if (maturityDate.isBefore(LocalDate.now())) throw new IllegalArgumentException("Asset maturity date cannot be in the past");
    }

    @Override
    public String toString() {
        return "Asset name = "
                + name
                + " | Type: " + assetType
                + " | Asset profitability = "
                + String.format("%.2f%%", profitability * 100)
                + " | Asset maturity date = "
                +  DateFormatter.formatDateToSlash(maturityDate);
    }

    public String getName() {
        return name;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public double getProfitability() {
        return profitability;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }
}
