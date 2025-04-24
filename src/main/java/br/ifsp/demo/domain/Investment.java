package br.ifsp.demo.domain;

public class Investment {
    private final double initialValue;
    private final double recurrentValue;
    private final Asset asset;

    public Investment(double initialValue, double recurrentValue, Asset asset) {
        verifyInvestment(initialValue, recurrentValue, asset);
        this.initialValue = initialValue;
        this.recurrentValue = recurrentValue;
        this.asset = asset;
    }

    @Override
    public String toString() {
        return "Initial value = " + initialValue + " | Recurrent value = " + recurrentValue + " | Asset name = " + asset.getName();
    }

    private void verifyInvestment(double initialValue, double recurrentValue, Asset asset){
        if (initialValue <= 0) throw new IllegalArgumentException("Initial value must be greater than zero");
        if (recurrentValue <= 0) throw new IllegalArgumentException("Recurrent value must be greater than zero");
        if (asset == null) throw new IllegalArgumentException("Asset cannot be null");
    }
}
