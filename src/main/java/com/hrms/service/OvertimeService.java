package com.hrms.service;

import com.hrms.dto.CreateOvertimeRequest;
import com.hrms.dto.OvertimeRequestDto;
import com.hrms.dto.UpdateOvertimeRequestStatus;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.model.Employee;
import com.hrms.model.OvertimeRequest;
import com.hrms.model.OvertimeStatus;
import com.hrms.model.RequestType;
import com.hrms.repository.EmployeeRepository;
import com.hrms.repository.OvertimeRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OvertimeService {

    private static final double MAX_OVERTIME_HOURS_PER_DAY = 12.0;

    @Autowired
    private OvertimeRequestRepository overtimeRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ApprovalResolutionService approvalResolutionService;

    @Transactional
    public OvertimeRequestDto createOvertimeRequest(CreateOvertimeRequest request) {
        Employee employee = employeeRepository.findByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with employeeId: " + request.getEmployeeId()));

        validateOvertimeRequest(request);

        boolean duplicate = overtimeRequestRepository.existsByEmployeeAndDateAndStatusIn(
                employee,
                request.getDate(),
                List.of(OvertimeStatus.PENDING_SUPERVISOR, OvertimeStatus.PENDING_HR, OvertimeStatus.APPROVED));
        if (duplicate) {
            throw new IllegalArgumentException(
                    "An overtime request for " + request.getDate() + " already exists (pending or approved).");
        }

        OvertimeRequest overtimeRequest = new OvertimeRequest();
        overtimeRequest.setEmployee(employee);
        overtimeRequest.setDate(request.getDate());
        overtimeRequest.setHours(request.getHours());
        overtimeRequest.setReason(request.getReason());
        overtimeRequest.setStatus(OvertimeStatus.PENDING_SUPERVISOR);

        OvertimeRequest savedRequest = overtimeRequestRepository.save(overtimeRequest);

        if (approvalResolutionService.resolveSupervisorApprover(employee, RequestType.OVERTIME).isEmpty()) {
            savedRequest.setStatus(OvertimeStatus.PENDING_HR);
            savedRequest = overtimeRequestRepository.save(savedRequest);
        }
        return mapToDto(savedRequest);
    }

    public List<OvertimeRequestDto> getAllOvertimeRequests() {
        return overtimeRequestRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public Page<OvertimeRequestDto> getAllOvertimeRequests(Pageable pageable) {
        return overtimeRequestRepository.findAll(pageable).map(this::mapToDto);
    }

    public OvertimeRequestDto getOvertimeRequestById(Long id) {
        OvertimeRequest request = overtimeRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Overtime request not found with id: " + id));
        return mapToDto(request);
    }

    @Transactional
    public OvertimeRequestDto updateOvertimeRequestStatus(Long id, UpdateOvertimeRequestStatus request) {
        OvertimeRequest overtimeRequest = overtimeRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Overtime request not found with id: " + id));

        overtimeRequest.setStatus(request.getStatus());

        OvertimeRequest updatedRequest = overtimeRequestRepository.save(overtimeRequest);
        return mapToDto(updatedRequest);
    }

    public List<OvertimeRequestDto> getOvertimeRequestsByEmployee(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with employeeId: " + employeeId));
        return overtimeRequestRepository.findByEmployee(employee).stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    public Page<OvertimeRequestDto> getOvertimeRequestsByEmployee(String employeeId, Pageable pageable) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with employeeId: " + employeeId));
        return overtimeRequestRepository.findByEmployee(employee, pageable).map(this::mapToDto);
    }

    public List<OvertimeRequestDto> getPendingOvertimeRequests() {
        return overtimeRequestRepository.findByStatusIn(java.util.List.of(
                OvertimeStatus.PENDING_SUPERVISOR, OvertimeStatus.PENDING_HR)).stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    public Page<OvertimeRequestDto> getPendingOvertimeRequests(Pageable pageable) {
        return overtimeRequestRepository.findByStatusIn(
                java.util.List.of(OvertimeStatus.PENDING_SUPERVISOR, OvertimeStatus.PENDING_HR), pageable)
                .map(this::mapToDto);
    }

    private void validateOvertimeRequest(CreateOvertimeRequest request) {
        if (request.getHours() == null || request.getHours() <= 0) {
            throw new IllegalArgumentException("Overtime hours must be greater than zero.");
        }
        if (request.getHours() > MAX_OVERTIME_HOURS_PER_DAY) {
            throw new IllegalArgumentException("Overtime cannot exceed " + MAX_OVERTIME_HOURS_PER_DAY + " hours per day.");
        }
    }

    private OvertimeRequestDto mapToDto(OvertimeRequest overtimeRequest) {
        OvertimeRequestDto dto = new OvertimeRequestDto();
        dto.setId(overtimeRequest.getId());
        dto.setEmployeeId(overtimeRequest.getEmployee().getEmployeeId());
        dto.setEmployeeName(overtimeRequest.getEmployee().getName());
        dto.setDate(overtimeRequest.getDate());
        dto.setHours(overtimeRequest.getHours());
        dto.setReason(overtimeRequest.getReason());
        dto.setStatus(overtimeRequest.getStatus());
        return dto;
    }
}
