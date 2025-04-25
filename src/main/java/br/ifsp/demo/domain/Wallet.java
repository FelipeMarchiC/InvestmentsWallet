package br.ifsp.demo.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Wallet {
    private final UUID id;
    private final List<Investment> investments;

    public Wallet() {
        id = UUID.randomUUID();
        investments = new ArrayList<>();
    }

    public void addInvestiment(Investment investment) {
        investments.add(investment);
    }

    public UUID getId() {
        return id;
    }

    public List<Investment> getInvestments() {
        return investments;
    }
}
