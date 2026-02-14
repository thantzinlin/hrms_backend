package com.hrms.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReportParamsRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer departmentId;  // optional filter
}
