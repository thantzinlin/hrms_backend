package com.hrms.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AttendanceReportRowDto {
    private Long attendanceId;
    private String employeeId;
    private String employeeName;
    private String departmentName;
    private LocalDate date;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Double workedHours;  // computed from checkIn/checkOut
}
