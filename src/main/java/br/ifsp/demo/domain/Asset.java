package br.ifsp.demo.domain;

public class Asset {
    private final String name;
    private final double profitability;

    public Asset(String name, double profitability) {
        verifyAsset(name, profitability);
        this.name = name;
        this.profitability = profitability;
    }

    private void verifyAsset(String name, double profitability){
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Asset name cannot be null or blank");
        if (profitability <= 0) throw new IllegalArgumentException("Asset profitability must be greater than zero");
    }

    @Override
    public String toString() {
        return "Asset name = " + name + " | Asset profitability = " + profitability;
    }

    public String getName() {
        return name;
    }

    public double getProfitability() {
        return profitability;
    }
}
