package br.ifsp.demo.domain;

import br.ifsp.demo.util.EffectiveWithdrawDateResolver;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InvestmentFactory {
    public static Investment createInvestmentWithPurchaseDate(double initialValue, Asset asset, LocalDate purchaseDate) {
        return new Investment(initialValue, asset, purchaseDate);
    }

    public static Investment createInvestmentWithPurchaseDateAndMockWithdraw(double value, Asset asset, LocalDate purchaseDate, LocalDate withdrawDate) {
        EffectiveWithdrawDateResolver dateResolver = mock(EffectiveWithdrawDateResolver.class);
        when(dateResolver.resolve(null)).thenReturn(withdrawDate);
        return new Investment(value, asset, purchaseDate, dateResolver);
    }
}
