package com.hrms.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OvertimeReportRowDto {
    private Long overtimeRequestId;
    private String employeeId;
    private String employeeName;
    private String departmentName;
    private LocalDate date;
    private Double hours;
    private String status;
    private String reason;
}
