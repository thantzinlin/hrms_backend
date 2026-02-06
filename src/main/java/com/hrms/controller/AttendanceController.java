package com.hrms.controller;

import com.hrms.dto.AttendanceDto;
import com.hrms.dto.CheckInRequest;
import com.hrms.dto.CheckOutRequest;
import com.hrms.service.AttendanceService;
import com.hrms.util.CustomApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/check-in")
    // @PreAuthorize("hasRole('EMPLOYEE') or hasRole('MANAGER') or hasRole('HR') or
    // hasRole('ADMIN')")
    public ResponseEntity<CustomApiResponse<AttendanceDto>> checkIn(@RequestBody CheckInRequest request) {
        AttendanceDto attendance = attendanceService.checkIn(request);

        return ResponseEntity.ok(CustomApiResponse.<AttendanceDto>builder().data(attendance).build());
    }

    @PostMapping("/check-out")
    // @PreAuthorize("hasRole('EMPLOYEE') or hasRole('MANAGER') or hasRole('HR') or
    // hasRole('ADMIN')")
    public ResponseEntity<CustomApiResponse<AttendanceDto>> checkOut(@RequestBody CheckOutRequest request) {
        AttendanceDto attendance = attendanceService.checkOut(request);
        return ResponseEntity.ok(CustomApiResponse.<AttendanceDto>builder().data(attendance).build());
    }

    @GetMapping("/report/employee/{employeeId}")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('HR') or hasRole('MANAGER')")
    public ResponseEntity<CustomApiResponse<List<AttendanceDto>>> getAttendanceByEmployeeAndDateRange(
            @PathVariable String employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AttendanceDto> report = attendanceService.getAttendanceByEmployeeAndDateRange(employeeId, startDate,
                endDate);
        return ResponseEntity.ok(CustomApiResponse.<List<AttendanceDto>>builder().data(report).build());
    }

    @GetMapping("/report/date/{date}")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('HR') or hasRole('MANAGER')")
    public ResponseEntity<CustomApiResponse<List<AttendanceDto>>> getAttendanceByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AttendanceDto> report = attendanceService.getAttendanceByDate(date);
        return ResponseEntity.ok(CustomApiResponse.<List<AttendanceDto>>builder().data(report).build());
    }
}
