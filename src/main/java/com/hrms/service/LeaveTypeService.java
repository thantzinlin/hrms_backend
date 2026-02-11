package com.hrms.service;

import com.hrms.dto.CreateLeaveTypeRequest;
import com.hrms.dto.LeaveTypeDto;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.model.LeaveType;
import com.hrms.repository.LeaveRequestRepository;
import com.hrms.repository.LeaveTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveTypeService {

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Transactional
    public LeaveTypeDto create(CreateLeaveTypeRequest request) {
        LeaveType entity = new LeaveType();
        entity.setCode(request.getCode().trim().toUpperCase());
        entity.setName(request.getName().trim());
        entity.setDescription(request.getDescription());
        entity.setMaxDays(request.getMaxDays());
        entity.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        LeaveType saved = leaveTypeRepository.save(entity);
        return mapToDto(saved);
    }

    public List<LeaveTypeDto> getAll() {
        return leaveTypeRepository.findAllByOrderByNameAsc()
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public Page<LeaveTypeDto> getPage(Pageable pageable) {
        return leaveTypeRepository.findAll(pageable).map(this::mapToDto);
    }

    public List<LeaveTypeDto> getActive() {
        return leaveTypeRepository.findByIsActiveTrueOrderByNameAsc()
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public LeaveTypeDto getById(Long id) {
        LeaveType entity = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found with id: " + id));
        return mapToDto(entity);
    }

    @Transactional
    public LeaveTypeDto update(Long id, CreateLeaveTypeRequest request) {
        LeaveType entity = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found with id: " + id));
        entity.setCode(request.getCode().trim().toUpperCase());
        entity.setName(request.getName().trim());
        entity.setDescription(request.getDescription());
        entity.setMaxDays(request.getMaxDays());
        entity.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        LeaveType saved = leaveTypeRepository.save(entity);
        return mapToDto(saved);
    }

    public void delete(Long id) {
        if (!leaveTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Leave type not found with id: " + id);
        }
        if (leaveRequestRepository.countByLeaveTypeId(id) > 0) {
            throw new IllegalStateException("Cannot delete leave type: it is used by existing leave requests.");
        }
        leaveTypeRepository.deleteById(id);
    }

    private LeaveTypeDto mapToDto(LeaveType entity) {
        LeaveTypeDto dto = new LeaveTypeDto();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setMaxDays(entity.getMaxDays());
        dto.setIsActive(entity.getIsActive());
        return dto;
    }
}
