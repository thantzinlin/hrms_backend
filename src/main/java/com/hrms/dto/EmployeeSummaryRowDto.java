package com.hrms.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EmployeeSummaryRowDto {
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private String email;
    private String departmentName;
    private String positionName;
    private String status;
    private LocalDateTime joinDate;
}
