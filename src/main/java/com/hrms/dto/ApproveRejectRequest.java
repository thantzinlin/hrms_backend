package com.hrms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApproveRejectRequest {
    @NotNull(message = "Request type (LEAVE or OVERTIME) is required")
    private com.hrms.model.RequestType requestType;

    @NotNull(message = "Request id is required")
    private Long requestId;

    /** Optional remarks for approval/rejection. */
    private String remarks;
}
