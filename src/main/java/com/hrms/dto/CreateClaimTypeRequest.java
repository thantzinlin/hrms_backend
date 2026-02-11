package com.hrms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateClaimTypeRequest {

    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;
    private BigDecimal maxAmountPerClaim;
    private Boolean requiresReceipt = false;
    private String currency = "USD";
    private Boolean isActive = true;
}
