package br.ifsp.demo.util;

import java.time.LocalDate;

public class EffectiveWithdrawDateResolver {

    public LocalDate resolve(LocalDate withdrawDate) {
        return withdrawDate != null ? withdrawDate : LocalDate.now();
    }
}
