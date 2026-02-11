package com.hrms.dto;

import com.hrms.model.ClaimStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ClaimDto {

    private Long id;
    private String claimNumber;
    private Long employeeId;
    private String employeeName;
    private String employeeEmployeeId;
    private Long claimTypeId;
    private String claimTypeCode;
    private String claimTypeName;
    private BigDecimal totalAmount;
    private String currency;
    private LocalDate claimDate;
    private String description;
    private ClaimStatus status;
    private LocalDateTime submittedAt;
    private Long approvedById;
    private String approvedByName;
    private LocalDateTime approvedAt;
    private String rejectionRemarks;
    private List<ClaimAttachmentDto> attachments = new ArrayList<>();
}
