package com.hrms.controller;

import com.hrms.dto.CreateOvertimeRequest;
import com.hrms.dto.OvertimeRequestDto;
import com.hrms.dto.UpdateOvertimeRequestStatus;
import com.hrms.service.OvertimeService;
import com.hrms.util.CustomApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/overtime")
public class OvertimeController {

    @Autowired
    private OvertimeService overtimeService;

    @PostMapping
    // @PreAuthorize("hasRole('EMPLOYEE') or hasRole('MANAGER') or hasRole('HR') or
    // hasRole('ADMIN')")
    public ResponseEntity<CustomApiResponse<OvertimeRequestDto>> createOvertimeRequest(
            @Valid @RequestBody CreateOvertimeRequest request) {
        OvertimeRequestDto createdRequest = overtimeService.createOvertimeRequest(request);
        return new ResponseEntity<>(CustomApiResponse.<OvertimeRequestDto>builder().data(createdRequest).build(),
                HttpStatus.CREATED);
    }

    @GetMapping
    // @PreAuthorize("hasRole('ADMIN') or hasRole('HR') or hasRole('MANAGER')")
    public ResponseEntity<CustomApiResponse<List<OvertimeRequestDto>>> getAllOvertimeRequests() {
        List<OvertimeRequestDto> requests = overtimeService.getAllOvertimeRequests();
        return ResponseEntity.ok(CustomApiResponse.<List<OvertimeRequestDto>>builder().data(requests).build());
    }

    @GetMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('HR') or hasRole('MANAGER') or
    // hasRole('EMPLOYEE')")
    public ResponseEntity<CustomApiResponse<OvertimeRequestDto>> getOvertimeRequestById(@PathVariable Long id) {
        OvertimeRequestDto request = overtimeService.getOvertimeRequestById(id);
        return ResponseEntity.ok(CustomApiResponse.<OvertimeRequestDto>builder().data(request).build());
    }

    @PutMapping("/{id}/status")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('HR') or hasRole('MANAGER')")
    public ResponseEntity<CustomApiResponse<OvertimeRequestDto>> updateOvertimeRequestStatus(
            @PathVariable Long id, @Valid @RequestBody UpdateOvertimeRequestStatus request) {
        OvertimeRequestDto updatedRequest = overtimeService.updateOvertimeRequestStatus(id, request);
        return ResponseEntity.ok(CustomApiResponse.<OvertimeRequestDto>builder().data(updatedRequest).build());
    }

    @GetMapping("/employee/{employeeId}")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('HR') or hasRole('MANAGER') or
    // hasRole('EMPLOYEE')")
    public ResponseEntity<CustomApiResponse<List<OvertimeRequestDto>>> getOvertimeRequestsByEmployee(
            @PathVariable String employeeId) {
        List<OvertimeRequestDto> requests = overtimeService.getOvertimeRequestsByEmployee(employeeId);
        return ResponseEntity.ok(CustomApiResponse.<List<OvertimeRequestDto>>builder().data(requests).build());
    }

    @GetMapping("/pending")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('HR') or hasRole('MANAGER')")
    public ResponseEntity<CustomApiResponse<List<OvertimeRequestDto>>> getPendingOvertimeRequests() {
        List<OvertimeRequestDto> requests = overtimeService.getPendingOvertimeRequests();
        return ResponseEntity.ok(CustomApiResponse.<List<OvertimeRequestDto>>builder().data(requests).build());
    }
}
