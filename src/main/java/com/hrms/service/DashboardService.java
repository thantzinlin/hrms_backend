package com.hrms.service;

import com.hrms.dto.DashboardStatsDto;
import com.hrms.model.Attendance;
import com.hrms.model.LeaveStatus;
import com.hrms.model.OvertimeStatus;
import com.hrms.repository.AttendanceRepository;
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
    private AttendanceRepository attendanceRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private OvertimeRequestRepository overtimeRequestRepository;

    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setTotalEmployees(employeeRepository.count());

        LocalDate today = LocalDate.now();
        List<Attendance> todayAttendance = attendanceRepository.findByDate(today);
        stats.setTodayAttendanceCount((long) todayAttendance.size());

        stats.setPendingLeaveCount(
                leaveRequestRepository.findByStatus(LeaveStatus.PENDING_SUPERVISOR).stream().count());
        stats.setPendingOvertimeCount(
                overtimeRequestRepository.findByStatus(OvertimeStatus.PENDING_SUPERVISOR).stream().count());

        return stats;
    }
}
