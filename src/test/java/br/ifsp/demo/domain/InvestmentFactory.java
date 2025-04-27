package br.ifsp.demo.domain;

import java.time.LocalDate;

public class InvestmentFactory {
    public static Investment createInvestmentWithPurchaseDate(double initialValue, Asset asset, LocalDate purchaseDate) {
        return new Investment(initialValue, asset, purchaseDate);
    }
}
