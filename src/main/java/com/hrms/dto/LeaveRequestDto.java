package com.hrms.dto;

import com.hrms.model.LeaveStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveRequestDto {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private LeaveStatus status;
    private Long leaveTypeId;
    private String leaveTypeCode;
    private String leaveTypeName;
}
