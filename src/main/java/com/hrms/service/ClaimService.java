package com.hrms.service;

import com.hrms.dto.*;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.model.*;
import com.hrms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

@Service
public class ClaimService {

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private ClaimTypeRepository claimTypeRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ApprovalResolutionService approvalResolutionService;

    @Transactional
    public ClaimDto create(String employeeId, CreateClaimRequest request) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));
        ClaimType claimType = claimTypeRepository.findById(request.getClaimTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Claim type not found with id: " + request.getClaimTypeId()));

        BigDecimal amount = request.getAmount();

        if (claimType.getMaxAmountPerClaim() != null && amount.compareTo(claimType.getMaxAmountPerClaim()) > 0) {
            throw new IllegalStateException("Amount exceeds maximum allowed (" + claimType.getMaxAmountPerClaim() + ") for this claim type.");
        }

        String claimNumber = generateClaimNumber();

        Claim claim = new Claim();
        claim.setClaimNumber(claimNumber);
        claim.setEmployee(employee);
        claim.setClaimType(claimType);
        claim.setTotalAmount(amount);
        claim.setCurrency(claimType.getCurrency() != null ? claimType.getCurrency() : "USD");
        claim.setClaimDate(request.getClaimDate());
        claim.setDescription(request.getDescription());
        claim.setStatus(ClaimStatus.DRAFT);

        Claim saved = claimRepository.save(claim);
        return mapToDto(saved);
    }

    @Transactional
    public ClaimDto submit(Long claimId, String employeeId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + claimId));

        if (!claim.getEmployee().getEmployeeId().equals(employeeId)) {
            throw new IllegalStateException("You can only submit your own claims.");
        }

        if (claim.getStatus() != ClaimStatus.DRAFT) {
            throw new IllegalStateException("Only draft claims can be submitted.");
        }

        ClaimType claimType = claim.getClaimType();
        if (Boolean.TRUE.equals(claimType.getRequiresReceipt()) && claim.getAttachments().isEmpty()) {
            throw new IllegalStateException("This claim type requires at least one receipt/attachment.");
        }

        claim.setStatus(ClaimStatus.PENDING_SUPERVISOR);
        claim.setSubmittedAt(LocalDateTime.now());

        if (approvalResolutionService.resolveSupervisorApprover(claim.getEmployee(), RequestType.CLAIM).isEmpty()) {
            claim.setStatus(ClaimStatus.PENDING_HR);
        }

        Claim saved = claimRepository.save(claim);
        return mapToDto(saved);
    }

    public ClaimDto getById(Long id) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id));
        return mapToDto(claim);
    }

    public Page<ClaimDto> getByEmployee(String employeeId, Pageable pageable) {
        return claimRepository.findByEmployee_EmployeeId(employeeId, pageable).map(this::mapToDto);
    }

    public Page<ClaimDto> getAll(Pageable pageable) {
        return claimRepository.findAll(pageable).map(this::mapToDto);
    }

    public Page<ClaimDto> getPending(Pageable pageable) {
        return claimRepository.findByStatusIn(
                List.of(ClaimStatus.PENDING_SUPERVISOR, ClaimStatus.PENDING_HR), pageable)
                .map(this::mapToDto);
    }

    private String generateClaimNumber() {
        int year = Year.now().getValue();
        long seq = claimRepository.findMaxId().orElse(0L) + 1;
        return String.format("CLM-%d-%04d", year, seq);
    }

    ClaimDto mapToDto(Claim claim) {
        ClaimDto dto = new ClaimDto();
        dto.setId(claim.getId());
        dto.setClaimNumber(claim.getClaimNumber());
        dto.setEmployeeId(claim.getEmployee().getId());
        dto.setEmployeeName(claim.getEmployee().getName());
        dto.setEmployeeEmployeeId(claim.getEmployee().getEmployeeId());
        dto.setClaimTypeId(claim.getClaimType().getId());
        dto.setClaimTypeCode(claim.getClaimType().getCode());
        dto.setClaimTypeName(claim.getClaimType().getName());
        dto.setTotalAmount(claim.getTotalAmount());
        dto.setCurrency(claim.getCurrency());
        dto.setClaimDate(claim.getClaimDate());
        dto.setDescription(claim.getDescription());
        dto.setStatus(claim.getStatus());
        dto.setSubmittedAt(claim.getSubmittedAt());
        if (claim.getApprovedBy() != null) {
            dto.setApprovedById(claim.getApprovedBy().getId());
            dto.setApprovedByName(claim.getApprovedBy().getName());
        }
        dto.setApprovedAt(claim.getApprovedAt());
        dto.setRejectionRemarks(claim.getRejectionRemarks());

        claim.getAttachments().forEach(a -> {
            ClaimAttachmentDto ad = new ClaimAttachmentDto();
            ad.setId(a.getId());
            ad.setFileName(a.getFileName());
            ad.setFileType(a.getFileType());
            ad.setFileSize(a.getFileSize());
            ad.setAttachmentType(a.getAttachmentType());
            dto.getAttachments().add(ad);
        });

        return dto;
    }
}
