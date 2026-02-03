package com.hrms.dto;

import lombok.Data;

@Data
public class DashboardStatsDto {
    private Long totalEmployees;
    private Long todayAttendanceCount;
    private Long pendingLeaveCount;
    private Long pendingOvertimeCount;
}
