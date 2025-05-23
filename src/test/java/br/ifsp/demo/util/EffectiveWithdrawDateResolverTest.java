package br.ifsp.demo.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class EffectiveWithdrawDateResolverTest {

    private final EffectiveWithdrawDateResolver resolver = new EffectiveWithdrawDateResolver();

    @Test
    @Tag("UnitTest")
    @Tag("Structural")
    @DisplayName("Should return the same date when withdraw date is not null")
    void shouldReturnSameDateWhenWithdrawDateIsNotNull() {
        LocalDate withdrawDate = LocalDate.of(2024, 12, 31);
        LocalDate result = resolver.resolve(withdrawDate);
        assertThat(result).isEqualTo(withdrawDate);
    }

    @Test
    @Tag("UnitTest")
    @Tag("Structural")
    @DisplayName("Should return current date when withdraw date is null")
    void shouldReturnCurrentDateWhenWithdrawDateIsNull() {
        LocalDate today = LocalDate.now();
        LocalDate result = resolver.resolve(null);

        assertThat(result).isEqualTo(today);
    }
}
