package com.hrms.service;

import com.hrms.dto.CreateLeaveRequest;
import com.hrms.dto.LeaveRequestDto;
import com.hrms.dto.UpdateLeaveRequestStatus;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.model.Employee;
import com.hrms.model.LeaveRequest;
import com.hrms.model.LeaveStatus;
import com.hrms.repository.EmployeeRepository;
import com.hrms.repository.LeaveRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Transactional
    public LeaveRequestDto createLeaveRequest(CreateLeaveRequest request) {
        Employee employee = employeeRepository.findByEmployeeId(request.getEmployeeId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Employee not found with id: " + request.getEmployeeId()));

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setStartDate(request.getStartDate());
        leaveRequest.setEndDate(request.getEndDate());
        leaveRequest.setReason(request.getReason());
        leaveRequest.setLeaveType(request.getLeaveType());
        leaveRequest.setStatus(LeaveStatus.PENDING);

        LeaveRequest savedRequest = leaveRequestRepository.save(leaveRequest);
        return mapToDto(savedRequest);
    }

    public List<LeaveRequestDto> getAllLeaveRequests() {
        return leaveRequestRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
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

    public List<LeaveRequestDto> getPendingLeaveRequests() {
        return leaveRequestRepository.findByStatus(LeaveStatus.PENDING).stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    public long calculateLeaveDays(Long id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));
        return ChronoUnit.DAYS.between(leaveRequest.getStartDate(), leaveRequest.getEndDate()) + 1;
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
        dto.setLeaveType(leaveRequest.getLeaveType());
        return dto;
    }
}
