package com.hrms.service;

import com.hrms.dto.CreateClaimTypeRequest;
import com.hrms.dto.ClaimTypeDto;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.model.ClaimType;
import com.hrms.repository.ClaimRepository;
import com.hrms.repository.ClaimTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClaimTypeService {

    @Autowired
    private ClaimTypeRepository claimTypeRepository;

    @Autowired
    private ClaimRepository claimRepository;

    @Transactional
    public ClaimTypeDto create(CreateClaimTypeRequest request) {
        String code = request.getCode().trim().toUpperCase();
        if (claimTypeRepository.existsByCode(code)) {
            throw new IllegalStateException("Claim type with code '" + code + "' already exists.");
        }
        ClaimType entity = new ClaimType();
        entity.setCode(code);
        entity.setName(request.getName().trim());
        entity.setDescription(request.getDescription());
        entity.setMaxAmountPerClaim(request.getMaxAmountPerClaim());
        entity.setRequiresReceipt(request.getRequiresReceipt() != null ? request.getRequiresReceipt() : false);
        entity.setCurrency(request.getCurrency() != null ? request.getCurrency().trim() : "USD");
        entity.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        ClaimType saved = claimTypeRepository.save(entity);
        return mapToDto(saved);
    }

    public List<ClaimTypeDto> getAll() {
        return claimTypeRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public Page<ClaimTypeDto> getPage(Pageable pageable) {
        return claimTypeRepository.findAll(pageable).map(this::mapToDto);
    }

    public List<ClaimTypeDto> getActive() {
        return claimTypeRepository.findByIsActiveTrueOrderByNameAsc().stream()
                .map(this::mapToDto).collect(Collectors.toList());
    }

    public ClaimTypeDto getById(Long id) {
        ClaimType entity = claimTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim type not found with id: " + id));
        return mapToDto(entity);
    }

    @Transactional
    public ClaimTypeDto update(Long id, CreateClaimTypeRequest request) {
        ClaimType entity = claimTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim type not found with id: " + id));
        String code = request.getCode().trim().toUpperCase();
        if (claimTypeRepository.existsByCodeAndIdNot(code, id)) {
            throw new IllegalStateException("Claim type with code '" + code + "' already exists.");
        }
        entity.setCode(code);
        entity.setName(request.getName().trim());
        entity.setDescription(request.getDescription());
        entity.setMaxAmountPerClaim(request.getMaxAmountPerClaim());
        entity.setRequiresReceipt(request.getRequiresReceipt() != null ? request.getRequiresReceipt() : false);
        entity.setCurrency(request.getCurrency() != null ? request.getCurrency().trim() : "USD");
        entity.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        ClaimType saved = claimTypeRepository.save(entity);
        return mapToDto(saved);
    }

    public void delete(Long id) {
        if (!claimTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Claim type not found with id: " + id);
        }
        if (claimRepository.countByClaimTypeId(id) > 0) {
            throw new IllegalStateException("Cannot delete claim type: it is used by existing claims.");
        }
        claimTypeRepository.deleteById(id);
    }

    private ClaimTypeDto mapToDto(ClaimType entity) {
        ClaimTypeDto dto = new ClaimTypeDto();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setMaxAmountPerClaim(entity.getMaxAmountPerClaim());
        dto.setRequiresReceipt(entity.getRequiresReceipt());
        dto.setCurrency(entity.getCurrency());
        dto.setIsActive(entity.getIsActive());
        return dto;
    }
}
