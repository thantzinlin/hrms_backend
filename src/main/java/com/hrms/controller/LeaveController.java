package com.hrms.controller;

import com.hrms.dto.CreateLeaveRequest;
import com.hrms.dto.LeaveRequestDto;
import com.hrms.dto.UpdateLeaveRequestStatus;
import com.hrms.service.LeaveService;
import com.hrms.util.CustomApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = { "/api/leave-requests", "/api/leaves" })
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @PostMapping
    // @PreAuthorize("hasRole('EMPLOYEE') or hasRole('MANAGER') or hasRole('HR') or
    // hasRole('ADMIN')")
    public ResponseEntity<CustomApiResponse<LeaveRequestDto>> createLeaveRequest(
            @Valid @RequestBody CreateLeaveRequest request) {
        LeaveRequestDto createdRequest = leaveService.createLeaveRequest(request);
        return new ResponseEntity<>(CustomApiResponse.<LeaveRequestDto>builder().data(createdRequest).build(),
                HttpStatus.CREATED);
    }

    @GetMapping
    // @PreAuthorize("hasRole('ADMIN') or hasRole('HR') or hasRole('MANAGER')")
    public ResponseEntity<CustomApiResponse<List<LeaveRequestDto>>> getAllLeaveRequests() {
        List<LeaveRequestDto> requests = leaveService.getAllLeaveRequests();
        return ResponseEntity.ok(CustomApiResponse.<List<LeaveRequestDto>>builder().data(requests).build());
    }

    @GetMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('HR') or hasRole('MANAGER') or
    // hasRole('EMPLOYEE')")
    public ResponseEntity<CustomApiResponse<LeaveRequestDto>> getLeaveRequestById(@PathVariable Long id) {
        LeaveRequestDto request = leaveService.getLeaveRequestById(id);
        return ResponseEntity.ok(CustomApiResponse.<LeaveRequestDto>builder().data(request).build());
    }

    @PutMapping("/{id}/status")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('HR') or hasRole('MANAGER')")
    public ResponseEntity<CustomApiResponse<LeaveRequestDto>> updateLeaveRequestStatus(
            @PathVariable Long id, @Valid @RequestBody UpdateLeaveRequestStatus request) {
        LeaveRequestDto updatedRequest = leaveService.updateLeaveRequestStatus(id, request);
        return ResponseEntity.ok(CustomApiResponse.<LeaveRequestDto>builder().data(updatedRequest).build());
    }

    @GetMapping("/employee/{employeeId}")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('HR') or hasRole('MANAGER') or
    // hasRole('EMPLOYEE')")
    public ResponseEntity<CustomApiResponse<List<LeaveRequestDto>>> getLeaveRequestsByEmployee(
            @PathVariable String employeeId) {
        List<LeaveRequestDto> requests = leaveService.getLeaveRequestsByEmployee(employeeId);
        return ResponseEntity.ok(CustomApiResponse.<List<LeaveRequestDto>>builder().data(requests).build());
    }

    @GetMapping("/pending")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('HR') or hasRole('MANAGER')")
    public ResponseEntity<CustomApiResponse<List<LeaveRequestDto>>> getPendingLeaveRequests() {
        List<LeaveRequestDto> requests = leaveService.getPendingLeaveRequests();
        return ResponseEntity.ok(CustomApiResponse.<List<LeaveRequestDto>>builder().data(requests).build());
    }

    @GetMapping("/{id}/calculate-days")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('HR') or hasRole('MANAGER') or
    // hasRole('EMPLOYEE')")
    public ResponseEntity<CustomApiResponse<Long>> calculateLeaveDays(@PathVariable Long id) {
        long leaveDays = leaveService.calculateLeaveDays(id);
        return ResponseEntity.ok(CustomApiResponse.<Long>builder().data(leaveDays).build());
    }
}
