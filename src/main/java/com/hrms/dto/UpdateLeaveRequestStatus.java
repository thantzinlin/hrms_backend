package com.hrms.dto;

import com.hrms.model.LeaveStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateLeaveRequestStatus {
    @NotNull
    private LeaveStatus status;
}
