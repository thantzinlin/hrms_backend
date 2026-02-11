package com.hrms.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateClaimRequest {

    @NotNull(message = "Claim type is required")
    private Long claimTypeId;

    @NotNull(message = "Claim date is required")
    private LocalDate claimDate;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private String description;
}
