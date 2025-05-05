package br.ifsp.demo.dto.investment;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record InvestmentWithdrawRequestDTO(
        @NotNull(message = "Investment ID cannot be null")
        UUID investmentId,

        @NotNull(message = "Purchase date must not be null")
        @Future(message = "Withdraw date must be in the future")
        LocalDate withdrawDate
) {
}
