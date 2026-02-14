package com.hrms.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveReportRowDto {
    private Long leaveRequestId;
    private String employeeId;
    private String employeeName;
    private String departmentName;
    private String leaveTypeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String reason;
}
