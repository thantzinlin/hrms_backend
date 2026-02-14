package com.hrms.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ClaimReportRowDto {
    private Long claimId;
    private String claimNumber;
    private String employeeId;
    private String employeeName;
    private String departmentName;
    private String claimTypeName;
    private LocalDate claimDate;
    private BigDecimal totalAmount;
    private String currency;
    private String status;
    private String description;
}
