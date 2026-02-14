package com.hrms.service;

import com.hrms.dto.*;
import com.hrms.model.*;
import com.hrms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private OvertimeRequestRepository overtimeRequestRepository;

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<AttendanceReportRowDto> getAttendanceReport(LocalDate startDate, LocalDate endDate, Integer departmentId) {
        List<Attendance> records = departmentId != null
                ? attendanceRepository.findByDepartmentAndDateBetween(departmentId, startDate, endDate)
                : attendanceRepository.findByDateBetweenWithEmployee(startDate, endDate);

        List<AttendanceReportRowDto> rows = new ArrayList<>();
        for (Attendance a : records) {
            AttendanceReportRowDto dto = new AttendanceReportRowDto();
            dto.setAttendanceId(a.getId());
            dto.setEmployeeId(a.getEmployee() != null ? a.getEmployee().getEmployeeId() : null);
            dto.setEmployeeName(a.getEmployee() != null ? a.getEmployee().getName() : null);
            dto.setDepartmentName(a.getEmployee() != null && a.getEmployee().getDepartment() != null
                    ? a.getEmployee().getDepartment().getName() : null);
            dto.setDate(a.getDate());
            dto.setCheckInTime(a.getCheckInTime());
            dto.setCheckOutTime(a.getCheckOutTime());
            if (a.getCheckInTime() != null && a.getCheckOutTime() != null) {
                Duration d = Duration.between(a.getCheckInTime(), a.getCheckOutTime());
                dto.setWorkedHours(d.toMinutes() / 60.0);
            } else {
                dto.setWorkedHours(null);
            }
            rows.add(dto);
        }
        return rows;
    }

    public List<LeaveReportRowDto> getLeaveReport(LocalDate startDate, LocalDate endDate, Integer departmentId) {
        List<LeaveRequest> records = departmentId != null
                ? leaveRequestRepository.findByDepartmentAndStartDateBetween(departmentId, startDate, endDate)
                : leaveRequestRepository.findByStartDateBetweenWithEmployeeAndLeaveType(startDate, endDate);

        return records.stream().map(this::mapToLeaveReportRow).collect(Collectors.toList());
    }

    private LeaveReportRowDto mapToLeaveReportRow(LeaveRequest lr) {
        LeaveReportRowDto dto = new LeaveReportRowDto();
        dto.setLeaveRequestId(lr.getId());
        dto.setEmployeeId(lr.getEmployee() != null ? lr.getEmployee().getEmployeeId() : null);
        dto.setEmployeeName(lr.getEmployee() != null ? lr.getEmployee().getName() : null);
        dto.setDepartmentName(lr.getEmployee() != null && lr.getEmployee().getDepartment() != null
                ? lr.getEmployee().getDepartment().getName() : null);
        dto.setLeaveTypeName(lr.getLeaveType() != null ? lr.getLeaveType().getName() : null);
        dto.setStartDate(lr.getStartDate());
        dto.setEndDate(lr.getEndDate());
        dto.setStatus(lr.getStatus() != null ? lr.getStatus().name() : null);
        dto.setReason(lr.getReason());
        return dto;
    }

    public List<OvertimeReportRowDto> getOvertimeReport(LocalDate startDate, LocalDate endDate, Integer departmentId) {
        List<OvertimeRequest> records = departmentId != null
                ? overtimeRequestRepository.findByDepartmentAndDateBetween(departmentId, startDate, endDate)
                : overtimeRequestRepository.findByDateBetweenWithEmployee(startDate, endDate);

        return records.stream().map(this::mapToOvertimeReportRow).collect(Collectors.toList());
    }

    private OvertimeReportRowDto mapToOvertimeReportRow(OvertimeRequest or) {
        OvertimeReportRowDto dto = new OvertimeReportRowDto();
        dto.setOvertimeRequestId(or.getId());
        dto.setEmployeeId(or.getEmployee() != null ? or.getEmployee().getEmployeeId() : null);
        dto.setEmployeeName(or.getEmployee() != null ? or.getEmployee().getName() : null);
        dto.setDepartmentName(or.getEmployee() != null && or.getEmployee().getDepartment() != null
                ? or.getEmployee().getDepartment().getName() : null);
        dto.setDate(or.getDate());
        dto.setHours(or.getHours());
        dto.setStatus(or.getStatus() != null ? or.getStatus().name() : null);
        dto.setReason(or.getReason());
        return dto;
    }

    public List<ClaimReportRowDto> getClaimReport(LocalDate startDate, LocalDate endDate, Integer departmentId) {
        List<Claim> records = departmentId != null
                ? claimRepository.findByDepartmentAndClaimDateBetween(departmentId, startDate, endDate)
                : claimRepository.findByClaimDateBetweenWithEmployeeAndClaimType(startDate, endDate);

        return records.stream().map(this::mapToClaimReportRow).collect(Collectors.toList());
    }

    private ClaimReportRowDto mapToClaimReportRow(Claim c) {
        ClaimReportRowDto dto = new ClaimReportRowDto();
        dto.setClaimId(c.getId());
        dto.setClaimNumber(c.getClaimNumber());
        dto.setEmployeeId(c.getEmployee() != null ? c.getEmployee().getEmployeeId() : null);
        dto.setEmployeeName(c.getEmployee() != null ? c.getEmployee().getName() : null);
        dto.setDepartmentName(c.getEmployee() != null && c.getEmployee().getDepartment() != null
                ? c.getEmployee().getDepartment().getName() : null);
        dto.setClaimTypeName(c.getClaimType() != null ? c.getClaimType().getName() : null);
        dto.setClaimDate(c.getClaimDate());
        dto.setTotalAmount(c.getTotalAmount());
        dto.setCurrency(c.getCurrency());
        dto.setStatus(c.getStatus() != null ? c.getStatus().name() : null);
        dto.setDescription(c.getDescription());
        return dto;
    }

    public List<EmployeeSummaryRowDto> getEmployeeSummaryReport(Integer departmentId) {
        List<Employee> employees = departmentId != null
                ? employeeRepository.findByDepartment_Id(departmentId)
                : employeeRepository.findAll();

        return employees.stream().map(this::mapToEmployeeSummaryRow).collect(Collectors.toList());
    }

    private EmployeeSummaryRowDto mapToEmployeeSummaryRow(Employee e) {
        EmployeeSummaryRowDto dto = new EmployeeSummaryRowDto();
        dto.setEmployeeId(e.getId());
        dto.setEmployeeCode(e.getEmployeeId());
        dto.setEmployeeName(e.getName());
        dto.setEmail(e.getEmail());
        dto.setDepartmentName(e.getDepartment() != null ? e.getDepartment().getName() : null);
        dto.setPositionName(e.getJobPosition() != null ? e.getJobPosition().getPositionName() : null);
        dto.setStatus(e.getStatus());
        dto.setJoinDate(e.getJoinDate());
        return dto;
    }
}
