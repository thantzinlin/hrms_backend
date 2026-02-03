package com.hrms.dto;

import com.hrms.model.OvertimeStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOvertimeRequestStatus {
    @NotNull
    private OvertimeStatus status;
}
