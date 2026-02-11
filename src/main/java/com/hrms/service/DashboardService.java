package com.hrms.service;

import com.hrms.dto.DashboardStatsDto;
import com.hrms.model.Attendance;
import com.hrms.model.LeaveStatus;
import com.hrms.model.OvertimeStatus;
import com.hrms.repository.AttendanceRepository;
import com.hrms.repository.DepartmentRepository;
import com.hrms.repository.EmployeeRepository;
import com.hrms.repository.LeaveRequestRepository;
import com.hrms.repository.OvertimeRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private OvertimeRequestRepository overtimeRequestRepository;

    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();
        long totalEmployees = employeeRepository.count();
        stats.setTotalEmployees(totalEmployees);
        stats.setTotalDepartments(departmentRepository.count());

        LocalDate today = LocalDate.now();
        List<Attendance> todayAttendance = attendanceRepository.findByDate(today);
        long todayCount = todayAttendance.size();
        stats.setTodayAttendanceCount(todayCount);

        stats.setPendingLeaveCount(leaveRequestRepository.countByStatus(LeaveStatus.PENDING_SUPERVISOR));
        stats.setPendingOvertimeCount(overtimeRequestRepository.countByStatus(OvertimeStatus.PENDING_SUPERVISOR));

        // Employee attendance rate: today's attendance / total employees (0–100)
        if (totalEmployees > 0) {
            stats.setEmployeeAttendanceRate(Math.min(100.0, (todayCount * 100.0) / totalEmployees));
        } else {
            stats.setEmployeeAttendanceRate(0.0);
        }

        // Leave approval rate: approved / total leave requests (0–100)
        long leaveTotal = leaveRequestRepository.count();
        if (leaveTotal > 0) {
            long leaveApproved = leaveRequestRepository.countByStatus(LeaveStatus.APPROVED);
            stats.setLeaveApprovalRate(Math.min(100.0, (leaveApproved * 100.0) / leaveTotal));
        } else {
            stats.setLeaveApprovalRate(0.0);
        }

        // Overtime utilization: approved / total OT requests (0–100)
        long otTotal = overtimeRequestRepository.count();
        if (otTotal > 0) {
            long otApproved = overtimeRequestRepository.countByStatus(OvertimeStatus.APPROVED);
            stats.setOvertimeUtilization(Math.min(100.0, (otApproved * 100.0) / otTotal));
        } else {
            stats.setOvertimeUtilization(0.0);
        }

        return stats;
    }
}
