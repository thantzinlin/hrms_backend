package com.hrms.dto;

import com.hrms.model.RequestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A single item in the pending approvals list (leave, overtime, or claim).
 * Id is composite: "LEAVE-1", "OVERTIME-2", "CLAIM-3" for use in approve/reject endpoints.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingApprovalItemDto {
    /** Composite id: requestType + "-" + requestId */
    private String id;
    private RequestType requestType;
    private Long requestId;
    private String requesterEmployeeId;
    private String requesterName;
    private LocalDate startDate;  // leave: start; overtime/claim: date
    private LocalDate endDate;    // leave only; null for overtime/claim
    private String reason;
    private String leaveType;     // leave only; null for overtime/claim
    private Double hours;         // overtime only; null for leave/claim
    /** Claim: total amount */
    private BigDecimal amount;
    /** Claim: claim type name */
    private String claimType;
}
