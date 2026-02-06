package com.hrms.service;

import com.hrms.dto.AttendanceDto;
import com.hrms.dto.CheckInRequest;
import com.hrms.dto.CheckOutRequest;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.model.Attendance;
import com.hrms.model.Employee;
import com.hrms.repository.AttendanceRepository;
import com.hrms.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Transactional
    public AttendanceDto checkIn(CheckInRequest request) {
        Employee employee = employeeRepository.findByUser_UserId(request.getEmployeeId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Employee not found with id: " + request.getEmployeeId()));

        LocalDate today = LocalDate.now();
        Optional<Attendance> existingAttendance = attendanceRepository.findByEmployeeAndDate(employee, today);

        if (existingAttendance.isPresent()) {
            throw new IllegalStateException("Already checked in for today.");
        }

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setDate(today);

        Attendance savedAttendance = attendanceRepository.save(attendance);
        return mapToDto(savedAttendance);
    }

    @Transactional
    public AttendanceDto checkOut(CheckOutRequest request) {
        Attendance attendance = attendanceRepository.findById(request.getAttendanceId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Attendance record not found with id: " + request.getAttendanceId()));

        if (attendance.getCheckOutTime() != null) {
            throw new IllegalStateException("Already checked out.");
        }

        attendance.setCheckOutTime(LocalDateTime.now());

        Attendance updatedAttendance = attendanceRepository.save(attendance);
        return mapToDto(updatedAttendance);
    }

    public List<AttendanceDto> getAttendanceByEmployeeAndDateRange(String employeeId, LocalDate startDate,
            LocalDate endDate) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        List<Attendance> attendanceList = attendanceRepository.findByEmployeeAndDateBetween(employee, startDate,
                endDate);
        return attendanceList.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<AttendanceDto> getAttendanceByDate(LocalDate date) {
        List<Attendance> attendanceList = attendanceRepository.findByDate(date);
        return attendanceList.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private AttendanceDto mapToDto(Attendance attendance) {
        AttendanceDto dto = new AttendanceDto();
        dto.setId(attendance.getId());
        dto.setEmployeeId(attendance.getEmployee().getId());
        dto.setCheckInTime(attendance.getCheckInTime());
        dto.setCheckOutTime(attendance.getCheckOutTime());
        dto.setDate(attendance.getDate());
        if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() != null) {
            Duration duration = Duration.between(attendance.getCheckInTime(), attendance.getCheckOutTime());
            dto.setWorkingHours(duration.toHoursPart() + (duration.toMinutesPart() / 60.0));
        }
        return dto;
    }
}
