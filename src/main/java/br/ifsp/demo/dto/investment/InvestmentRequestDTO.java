package br.ifsp.demo.dto.investment;

import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

public record InvestmentRequestDTO(
        @NotNull(message = "The investment ID must not be null")
        UUID id,

        @NotNull(message = "The initial value is required")
        @Positive(message = "The initial value must be positive")
        Double initialValue,

        @NotNull(message = "Purchase date must not be null")
        @PastOrPresent(message = "Purchase date must be in past or present")
        LocalDate purchaseDate,

        @NotNull(message = "Withdraw date must not be null")
        @Future(message = "Withdraw date must be in the future")
        LocalDate withdrawDate,

        @NotNull(message = "The asset ID must not be null")
        UUID assetId,

        @NotNull(message = "The wallet ID must not be null")
        @Valid
        UUID walletId
) {
}