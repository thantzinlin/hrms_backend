package com.hrms.controller;

import com.hrms.dto.ApproveRejectRequest;
import com.hrms.dto.PendingApprovalItemDto;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.model.Employee;
import com.hrms.model.RequestType;
import com.hrms.repository.EmployeeRepository;
import com.hrms.security.services.UserDetailsImpl;
import com.hrms.service.ApprovalWorkflowService;
import com.hrms.util.CustomApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {

    @Autowired
    private ApprovalWorkflowService approvalWorkflowService;
    @Autowired
    private EmployeeRepository employeeRepository;

    /** Pending items for the current user (as supervisor or HR). */
    @GetMapping("/pending")
    public ResponseEntity<CustomApiResponse<List<PendingApprovalItemDto>>> getPending(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Employee approver = employeeRepository.findByUser_UserId(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for current user"));
        List<PendingApprovalItemDto> pending = approvalWorkflowService.getPendingForApprover(approver);
        return ResponseEntity.ok(CustomApiResponse.<List<PendingApprovalItemDto>>builder().data(pending).build());
    }

    /** Approve by composite id (e.g. LEAVE-1, OVERTIME-2). */
    @PostMapping("/{id}/approve")
    public ResponseEntity<CustomApiResponse<Void>> approveById(
            @PathVariable String id,
            @RequestBody(required = false) ApproveRejectRequest body,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ApproveRejectRequest req = parseId(id, body);
        Employee approver = employeeRepository.findByUser_UserId(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for current user"));
        approvalWorkflowService.approve(req.getRequestType(), req.getRequestId(), approver, req.getRemarks());
        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomApiResponse.<Void>builder().returnMessage("Approved successfully").build());
    }

    /** Reject by composite id (e.g. LEAVE-1, OVERTIME-2). */
    @PostMapping("/{id}/reject")
    public ResponseEntity<CustomApiResponse<Void>> rejectById(
            @PathVariable String id,
            @RequestBody(required = false) ApproveRejectRequest body,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ApproveRejectRequest req = parseId(id, body);
        Employee approver = employeeRepository.findByUser_UserId(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for current user"));
        approvalWorkflowService.reject(req.getRequestType(), req.getRequestId(), approver, req.getRemarks());
        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomApiResponse.<Void>builder().returnMessage("Rejected").build());
    }

    /** Approve with body (requestType, requestId, remarks). */
    @PostMapping("/approve")
    public ResponseEntity<CustomApiResponse<Void>> approve(
            @Valid @RequestBody ApproveRejectRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Employee approver = employeeRepository.findByUser_UserId(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for current user"));
        approvalWorkflowService.approve(request.getRequestType(), request.getRequestId(), approver, request.getRemarks());
        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomApiResponse.<Void>builder().returnMessage("Approved successfully").build());
    }

    /** Reject with body (requestType, requestId, remarks). */
    @PostMapping("/reject")
    public ResponseEntity<CustomApiResponse<Void>> reject(
            @Valid @RequestBody ApproveRejectRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Employee approver = employeeRepository.findByUser_UserId(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for current user"));
        approvalWorkflowService.reject(request.getRequestType(), request.getRequestId(), approver, request.getRemarks());
        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomApiResponse.<Void>builder().returnMessage("Rejected").build());
    }

    private ApproveRejectRequest parseId(String id, ApproveRejectRequest body) {
        int dash = id.indexOf('-');
        if (dash <= 0 || dash == id.length() - 1) {
            throw new IllegalArgumentException("Invalid approval id; expected format: LEAVE-1, OVERTIME-2, or CLAIM-3");
        }
        RequestType type = RequestType.valueOf(id.substring(0, dash));
        Long requestId = Long.parseLong(id.substring(dash + 1));
        ApproveRejectRequest req = body != null ? body : new ApproveRejectRequest();
        req.setRequestType(type);
        req.setRequestId(requestId);
        if (req.getRemarks() == null) req.setRemarks("");
        return req;
    }
}
