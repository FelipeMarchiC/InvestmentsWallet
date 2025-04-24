package br.ifsp.demo.domain;

public class Investment {
    private final double initialValue;
    private final double recurrentValue;
    private final Asset asset;

    public Investment(double initialValue, double recurrentValue, Asset asset) {
        this.initialValue = initialValue;
        this.recurrentValue = recurrentValue;
        this.asset = asset;
    }
}
