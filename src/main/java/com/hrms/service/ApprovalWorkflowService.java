package com.hrms.service;

import com.hrms.dto.PendingApprovalItemDto;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.model.*;
import com.hrms.repository.ApprovalHistoryRepository;
import com.hrms.repository.LeaveRequestRepository;
import com.hrms.repository.OvertimeRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles approval workflow: move status between stages, record history.
 * Approvers are resolved dynamically (no approver stored on request tables).
 */
@Service
public class ApprovalWorkflowService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;
    @Autowired
    private OvertimeRequestRepository overtimeRequestRepository;
    @Autowired
    private ApprovalHistoryRepository approvalHistoryRepository;
    @Autowired
    private ApprovalResolutionService resolutionService;

    /** Pending items where the given employee is the next approver (supervisor or HR). */
    @Transactional(readOnly = true)
    public List<PendingApprovalItemDto> getPendingForApprover(Employee approver) {
        if (approver == null) return List.of();
        List<PendingApprovalItemDto> result = new ArrayList<>();

        // Supervisor-level: leave/overtime in PENDING_SUPERVISOR where I am the resolved supervisor
        // (Requester must have reporting_to set; supervisor must have approval_authorities.can_approve_leave/overtime = true)
        List<LeaveRequest> leaveSupervisor = leaveRequestRepository.findByStatusWithEmployeeAndReportingTo(LeaveStatus.PENDING_SUPERVISOR);
        for (LeaveRequest lr : leaveSupervisor) {
            Optional<Employee> sup = resolutionService.resolveSupervisorApprover(lr.getEmployee(), RequestType.LEAVE);
            if (sup.map(s -> s.getId().equals(approver.getId())).orElse(false)) {
                result.add(toPendingDto(lr));
            }
        }
        List<OvertimeRequest> otSupervisor = overtimeRequestRepository.findByStatusWithEmployeeAndReportingTo(OvertimeStatus.PENDING_SUPERVISOR);
        for (OvertimeRequest or : otSupervisor) {
            Optional<Employee> sup = resolutionService.resolveSupervisorApprover(or.getEmployee(), RequestType.OVERTIME);
            if (sup.map(s -> s.getId().equals(approver.getId())).orElse(false)) {
                result.add(toPendingDto(or));
            }
        }

        // HR-level: leave/overtime in PENDING_HR and I am HR
        if (Boolean.TRUE.equals(resolutionService.isHrApprover(approver))) {
            leaveRequestRepository.findByStatus(LeaveStatus.PENDING_HR).stream()
                    .map(this::toPendingDto)
                    .forEach(result::add);
            overtimeRequestRepository.findByStatus(OvertimeStatus.PENDING_HR).stream()
                    .map(this::toPendingDto)
                    .forEach(result::add);
        }
        return result;
    }

    @Transactional
    public void approveAsSupervisor(RequestType requestType, Long requestId, Employee approver, String remarks) {
        if (requestType == RequestType.LEAVE) {
            LeaveRequest lr = leaveRequestRepository.findById(requestId)
                    .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + requestId));
            if (lr.getStatus() != LeaveStatus.PENDING_SUPERVISOR) {
                throw new IllegalStateException("Leave request is not pending supervisor approval");
            }
            if (!resolutionService.canApproveAsSupervisor(approver, RequestType.LEAVE)) {
                throw new IllegalStateException("Employee is not authorized to approve leave as supervisor");
            }
            lr.setStatus(LeaveStatus.PENDING_HR);
            leaveRequestRepository.save(lr);
            recordHistory(RequestType.LEAVE, requestId, approver, ApprovalLevel.SUPERVISOR, ApprovalAction.APPROVED, remarks);
        } else {
            OvertimeRequest or = overtimeRequestRepository.findById(requestId)
                    .orElseThrow(() -> new ResourceNotFoundException("Overtime request not found: " + requestId));
            if (or.getStatus() != OvertimeStatus.PENDING_SUPERVISOR) {
                throw new IllegalStateException("Overtime request is not pending supervisor approval");
            }
            if (!resolutionService.canApproveAsSupervisor(approver, RequestType.OVERTIME)) {
                throw new IllegalStateException("Employee is not authorized to approve overtime as supervisor");
            }
            or.setStatus(OvertimeStatus.PENDING_HR);
            overtimeRequestRepository.save(or);
            recordHistory(RequestType.OVERTIME, requestId, approver, ApprovalLevel.SUPERVISOR, ApprovalAction.APPROVED, remarks);
        }
    }

    @Transactional
    public void approveAsHr(RequestType requestType, Long requestId, Employee approver, String remarks) {
        if (!resolutionService.isHrApprover(approver)) {
            throw new IllegalStateException("Employee is not authorized as HR approver");
        }
        if (requestType == RequestType.LEAVE) {
            LeaveRequest lr = leaveRequestRepository.findById(requestId)
                    .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + requestId));
            if (lr.getStatus() != LeaveStatus.PENDING_HR) {
                throw new IllegalStateException("Leave request is not pending HR approval");
            }
            lr.setStatus(LeaveStatus.APPROVED);
            leaveRequestRepository.save(lr);
            recordHistory(RequestType.LEAVE, requestId, approver, ApprovalLevel.HR, ApprovalAction.APPROVED, remarks);
        } else {
            OvertimeRequest or = overtimeRequestRepository.findById(requestId)
                    .orElseThrow(() -> new ResourceNotFoundException("Overtime request not found: " + requestId));
            if (or.getStatus() != OvertimeStatus.PENDING_HR) {
                throw new IllegalStateException("Overtime request is not pending HR approval");
            }
            or.setStatus(OvertimeStatus.APPROVED);
            overtimeRequestRepository.save(or);
            recordHistory(RequestType.OVERTIME, requestId, approver, ApprovalLevel.HR, ApprovalAction.APPROVED, remarks);
        }
    }

    @Transactional
    public void reject(RequestType requestType, Long requestId, Employee approver, String remarks) {
        if (requestType == RequestType.LEAVE) {
            LeaveRequest lr = leaveRequestRepository.findById(requestId)
                    .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + requestId));
            if (lr.getStatus() == LeaveStatus.APPROVED || lr.getStatus() == LeaveStatus.REJECTED) {
                throw new IllegalStateException("Leave request is already in final state");
            }
            ApprovalLevel level = lr.getStatus() == LeaveStatus.PENDING_SUPERVISOR ? ApprovalLevel.SUPERVISOR : ApprovalLevel.HR;
            ensureCanActAtLevel(approver, RequestType.LEAVE, lr.getEmployee(), level);
            lr.setStatus(LeaveStatus.REJECTED);
            leaveRequestRepository.save(lr);
            recordHistory(RequestType.LEAVE, requestId, approver, level, ApprovalAction.REJECTED, remarks);
        } else {
            OvertimeRequest or = overtimeRequestRepository.findById(requestId)
                    .orElseThrow(() -> new ResourceNotFoundException("Overtime request not found: " + requestId));
            if (or.getStatus() == OvertimeStatus.APPROVED || or.getStatus() == OvertimeStatus.REJECTED) {
                throw new IllegalStateException("Overtime request is already in final state");
            }
            ApprovalLevel level = or.getStatus() == OvertimeStatus.PENDING_SUPERVISOR ? ApprovalLevel.SUPERVISOR : ApprovalLevel.HR;
            ensureCanActAtLevel(approver, RequestType.OVERTIME, or.getEmployee(), level);
            or.setStatus(OvertimeStatus.REJECTED);
            overtimeRequestRepository.save(or);
            recordHistory(RequestType.OVERTIME, requestId, approver, level, ApprovalAction.REJECTED, remarks);
        }
    }

    private void ensureCanActAtLevel(Employee approver, RequestType requestType, Employee requester, ApprovalLevel level) {
        if (level == ApprovalLevel.SUPERVISOR) {
            Optional<Employee> resolved = resolutionService.resolveSupervisorApprover(requester, requestType);
            if (resolved.isEmpty() || !resolved.get().getId().equals(approver.getId())) {
                throw new IllegalStateException("You are not the designated supervisor approver for this request");
            }
            if (!resolutionService.canApproveAsSupervisor(approver, requestType)) {
                throw new IllegalStateException("Employee is not authorized to act as supervisor for this request type");
            }
        } else {
            if (!resolutionService.isHrApprover(approver)) {
                throw new IllegalStateException("Employee is not authorized as HR approver");
            }
        }
    }

    /** Route approve to supervisor or HR based on current status. */
    @Transactional
    public void approve(RequestType requestType, Long requestId, Employee approver, String remarks) {
        if (requestType == RequestType.LEAVE) {
            LeaveRequest lr = leaveRequestRepository.findById(requestId)
                    .orElseThrow(() -> new ResourceNotFoundException("Leave request not found: " + requestId));
            if (lr.getStatus() == LeaveStatus.PENDING_SUPERVISOR) {
                approveAsSupervisor(RequestType.LEAVE, requestId, approver, remarks);
            } else if (lr.getStatus() == LeaveStatus.PENDING_HR) {
                approveAsHr(RequestType.LEAVE, requestId, approver, remarks);
            } else {
                throw new IllegalStateException("Leave request is not in a pending approval state");
            }
        } else {
            OvertimeRequest or = overtimeRequestRepository.findById(requestId)
                    .orElseThrow(() -> new ResourceNotFoundException("Overtime request not found: " + requestId));
            if (or.getStatus() == OvertimeStatus.PENDING_SUPERVISOR) {
                approveAsSupervisor(RequestType.OVERTIME, requestId, approver, remarks);
            } else if (or.getStatus() == OvertimeStatus.PENDING_HR) {
                approveAsHr(RequestType.OVERTIME, requestId, approver, remarks);
            } else {
                throw new IllegalStateException("Overtime request is not in a pending approval state");
            }
        }
    }

    private void recordHistory(RequestType requestType, Long requestId, Employee approver,
                               ApprovalLevel level, ApprovalAction action, String remarks) {
        ApprovalHistory h = new ApprovalHistory();
        h.setRequestType(requestType);
        h.setRequestId(requestId);
        h.setApprover(approver);
        h.setApprovalLevel(level);
        h.setAction(action);
        h.setActionDate(LocalDateTime.now());
        h.setRemarks(remarks);
        h.setCreatedAt(LocalDateTime.now());
        approvalHistoryRepository.save(h);
    }

    private PendingApprovalItemDto toPendingDto(LeaveRequest lr) {
        return PendingApprovalItemDto.builder()
                .id(RequestType.LEAVE.name() + "-" + lr.getId())
                .requestType(RequestType.LEAVE)
                .requestId(lr.getId())
                .requesterEmployeeId(lr.getEmployee().getEmployeeId())
                .requesterName(lr.getEmployee().getName())
                .startDate(lr.getStartDate())
                .endDate(lr.getEndDate())
                .reason(lr.getReason())
                .leaveType(lr.getLeaveType() != null ? lr.getLeaveType().getName() : null)
                .build();
    }

    private PendingApprovalItemDto toPendingDto(OvertimeRequest or) {
        return PendingApprovalItemDto.builder()
                .id(RequestType.OVERTIME.name() + "-" + or.getId())
                .requestType(RequestType.OVERTIME)
                .requestId(or.getId())
                .requesterEmployeeId(or.getEmployee().getEmployeeId())
                .requesterName(or.getEmployee().getName())
                .startDate(or.getDate())
                .reason(or.getReason())
                .hours(or.getHours())
                .build();
    }
}
