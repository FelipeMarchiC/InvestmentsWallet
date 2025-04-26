package br.ifsp.demo.domain;

import java.util.UUID;

public class Investment {
    private final double initialValue;
    private final Asset asset;
    private final UUID id;

    public Investment(double initialValue, Asset asset) {
        verifyInvestment(initialValue, asset);
        this.initialValue = initialValue;
        this.asset = asset;
        this.id = UUID.randomUUID();
    }

    @Override
    public String toString() {
        return "Initial value = " + initialValue + " | " + asset.toString();
    }

    private void verifyInvestment(double initialValue, Asset asset){
        if (initialValue <= 0) throw new IllegalArgumentException("Initial value must be greater than zero");
        if (asset == null) throw new IllegalArgumentException("Asset cannot be null");
    }

    public UUID getId() {
        return this.id;
    }
}
