package com.hrms.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClaimTypeDto {
    private Long id;
    private String code;
    private String name;
    private String description;
    private BigDecimal maxAmountPerClaim;
    private Boolean requiresReceipt;
    private String currency;
    private Boolean isActive;
}
