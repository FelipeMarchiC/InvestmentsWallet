package br.ifsp.demo.domain;

import java.util.ArrayList;
import java.util.List;

public class Wallet {
    private final List<Investment> investments;

    public Wallet() {
        this.investments = new ArrayList<>();
    }

    public boolean addInvestment(Investment investment) {
        investments.add(investment);
        return true;
    }
}
