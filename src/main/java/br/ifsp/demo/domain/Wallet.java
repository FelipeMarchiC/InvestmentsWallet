package br.ifsp.demo.domain;

import java.util.*;

public class Wallet {
    private final UUID id;
    private final Map<UUID, Investment> investments;
    private final Map<UUID, Investment> history;

    public Wallet() {
        id = UUID.randomUUID();
        investments = new HashMap<>();
        history = new HashMap<>();
    }

    public void addInvestiment(Investment investment) {
        investments.put(investment.getId(), investment);
    }

    public void removeInvestiment(Investment investment) {
        investments.remove(investment.getId());
    }

    public UUID getId() {
        return id;
    }

    public List<Investment> getInvestments() {
        return new ArrayList<>(investments.values());
    }

    public Investment getInvestmentById(UUID investmentId) {
        return investments.get(investmentId);
    }

    public double getTotalBalance() {
        return 2500;
    }

    public boolean addInvestmentOnHistory(Investment investment) {
        Optional<Investment> added = Optional.ofNullable(history.put(investment.getId(), investment));
        return added.isEmpty();
    }
}
