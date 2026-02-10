package com.hrms.dto;

import com.hrms.model.RequestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * A single item in the pending approvals list (leave or overtime).
 * Id is composite: "LEAVE-1" or "OVERTIME-2" for use in approve/reject endpoints.
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
    private LocalDate startDate;  // leave: start; overtime: date
    private LocalDate endDate;    // leave only; null for overtime
    private String reason;
    private String leaveType;     // leave only; null for overtime
    private Double hours;         // overtime only; null for leave
}
