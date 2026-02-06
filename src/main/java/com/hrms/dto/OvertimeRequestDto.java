package com.hrms.dto;

import com.hrms.model.OvertimeStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OvertimeRequestDto {
    private Long id;
    private String employeeId;
    private String employeeName;
    private LocalDate date;
    private Double hours;
    private String reason;
    private OvertimeStatus status;
}
