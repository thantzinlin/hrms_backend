package com.hrms.service;

import com.hrms.dto.CreateLeaveRequest;
import com.hrms.dto.LeaveRequestDto;
import com.hrms.dto.UpdateLeaveRequestStatus;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.model.Employee;
import com.hrms.model.LeaveRequest;
import com.hrms.model.LeaveStatus;
import com.hrms.model.LeaveType;
import com.hrms.model.RequestType;
import com.hrms.repository.EmployeeRepository;
import com.hrms.repository.LeaveRequestRepository;
import com.hrms.repository.LeaveTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveService {

    private static final int MAX_LEAVE_DAYS_PER_REQUEST = 90;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private ApprovalResolutionService approvalResolutionService;

    @Transactional
    public LeaveRequestDto createLeaveRequest(CreateLeaveRequest request) {
        Employee employee = employeeRepository.findByEmployeeId(request.getEmployeeId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Employee not found with id: " + request.getEmployeeId()));

        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found with id: " + request.getLeaveTypeId()));

        validateLeaveRequest(request);

        List<LeaveRequest> overlapping = leaveRequestRepository.findOverlappingLeave(
                employee,
                request.getStartDate(),
                request.getEndDate(),
                List.of(LeaveStatus.PENDING_SUPERVISOR, LeaveStatus.PENDING_HR, LeaveStatus.APPROVED));
        if (!overlapping.isEmpty()) {
            throw new IllegalArgumentException(
                    "Leave request overlaps with an existing leave (pending or approved) for dates "
                            + overlapping.get(0).getStartDate() + " to " + overlapping.get(0).getEndDate() + ".");
        }

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setStartDate(request.getStartDate());
        leaveRequest.setEndDate(request.getEndDate());
        leaveRequest.setReason(request.getReason());
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setStatus(LeaveStatus.PENDING_SUPERVISOR);

        LeaveRequest savedRequest = leaveRequestRepository.save(leaveRequest);

        if (approvalResolutionService.resolveSupervisorApprover(employee, RequestType.LEAVE).isEmpty()) {
            savedRequest.setStatus(LeaveStatus.PENDING_HR);
            savedRequest = leaveRequestRepository.save(savedRequest);
        }
        return mapToDto(savedRequest);
    }

    public List<LeaveRequestDto> getAllLeaveRequests() {
        return leaveRequestRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public Page<LeaveRequestDto> getAllLeaveRequests(Pageable pageable) {
        return leaveRequestRepository.findAll(pageable).map(this::mapToDto);
    }

    public LeaveRequestDto getLeaveRequestById(Long id) {
        LeaveRequest request = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));
        return mapToDto(request);
    }

    @Transactional
    public LeaveRequestDto updateLeaveRequestStatus(Long id, UpdateLeaveRequestStatus request) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));

        leaveRequest.setStatus(request.getStatus());

        LeaveRequest updatedRequest = leaveRequestRepository.save(leaveRequest);
        return mapToDto(updatedRequest);
    }

    public List<LeaveRequestDto> getLeaveRequestsByEmployee(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        return leaveRequestRepository.findByEmployee(employee).stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    public Page<LeaveRequestDto> getLeaveRequestsByEmployee(String employeeId, Pageable pageable) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        return leaveRequestRepository.findByEmployee(employee, pageable).map(this::mapToDto);
    }

    public List<LeaveRequestDto> getPendingLeaveRequests() {
        return leaveRequestRepository.findByStatusIn(java.util.List.of(
                LeaveStatus.PENDING_SUPERVISOR, LeaveStatus.PENDING_HR)).stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    public Page<LeaveRequestDto> getPendingLeaveRequests(Pageable pageable) {
        return leaveRequestRepository.findByStatusIn(
                java.util.List.of(LeaveStatus.PENDING_SUPERVISOR, LeaveStatus.PENDING_HR), pageable)
                .map(this::mapToDto);
    }

    public long calculateLeaveDays(Long id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));
        return ChronoUnit.DAYS.between(leaveRequest.getStartDate(), leaveRequest.getEndDate()) + 1;
    }

    private void validateLeaveRequest(CreateLeaveRequest request) {
        LocalDate start = request.getStartDate();
        LocalDate end = request.getEndDate();
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date must be on or after start date.");
        }
        long days = ChronoUnit.DAYS.between(start, end) + 1;
        if (days > MAX_LEAVE_DAYS_PER_REQUEST) {
            throw new IllegalArgumentException("Leave cannot exceed " + MAX_LEAVE_DAYS_PER_REQUEST + " days per request.");
        }
    }

    private LeaveRequestDto mapToDto(LeaveRequest leaveRequest) {
        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setId(leaveRequest.getId());
        dto.setEmployeeId(leaveRequest.getEmployee().getId());
        dto.setEmployeeName(leaveRequest.getEmployee().getName());
        dto.setStartDate(leaveRequest.getStartDate());
        dto.setEndDate(leaveRequest.getEndDate());
        dto.setReason(leaveRequest.getReason());
        dto.setStatus(leaveRequest.getStatus());
        if (leaveRequest.getLeaveType() != null) {
            dto.setLeaveTypeId(leaveRequest.getLeaveType().getId());
            dto.setLeaveTypeCode(leaveRequest.getLeaveType().getCode());
            dto.setLeaveTypeName(leaveRequest.getLeaveType().getName());
        }
        return dto;
    }
}
