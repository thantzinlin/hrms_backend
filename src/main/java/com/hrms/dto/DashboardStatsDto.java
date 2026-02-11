package com.hrms.dto;

import lombok.Data;

@Data
public class DashboardStatsDto {
    private Long totalEmployees;
    private Long totalDepartments;
    private Long todayAttendanceCount;
    private Long pendingLeaveCount;
    private Long pendingOvertimeCount;
    private Long pendingClaimCount = 0L;
    /** Employee attendance rate (0-100): today's attendance / total employees. */
    private Double employeeAttendanceRate;
    /** Leave approval rate (0-100): approved leaves / total leave requests. */
    private Double leaveApprovalRate;
    /** Overtime utilization (0-100): approved OT / total OT requests. */
    private Double overtimeUtilization;
}
