package com.hrms.controller;

import com.hrms.dto.ClaimDto;
import com.hrms.dto.CreateClaimRequest;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.model.Employee;
import com.hrms.repository.EmployeeRepository;
import com.hrms.security.services.UserDetailsImpl;
import com.hrms.service.ClaimAttachmentService;
import com.hrms.service.ClaimService;
import com.hrms.service.ClaimTypeService;
import com.hrms.util.CustomApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    @Autowired
    private ClaimService claimService;

    @Autowired
    private ClaimTypeService claimTypeService;

    @Autowired
    private ClaimAttachmentService claimAttachmentService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/types")
    public ResponseEntity<CustomApiResponse<?>> getActiveClaimTypes() {
        return ResponseEntity.ok(CustomApiResponse.builder().data(claimTypeService.getActive()).build());
    }

    @PostMapping
    public ResponseEntity<CustomApiResponse<ClaimDto>> create(
            @Valid @RequestBody CreateClaimRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String employeeId = resolveEmployeeId(userDetails);
        ClaimDto created = claimService.create(employeeId, request);
        return new ResponseEntity<>(CustomApiResponse.<ClaimDto>builder().data(created).build(), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<CustomApiResponse<ClaimDto>> submit(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String employeeId = resolveEmployeeId(userDetails);
        ClaimDto updated = claimService.submit(id, employeeId);
        return ResponseEntity.ok(CustomApiResponse.<ClaimDto>builder().data(updated).returnMessage("Claim submitted successfully").build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse<ClaimDto>> getById(@PathVariable Long id) {
        ClaimDto claim = claimService.getById(id);
        return ResponseEntity.ok(CustomApiResponse.<ClaimDto>builder().data(claim).build());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<CustomApiResponse<Page<ClaimDto>>> getByEmployee(
            @PathVariable String employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "claimDate,desc") String sort) {
        Sort order = parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, order);
        Page<ClaimDto> claims = claimService.getByEmployee(employeeId, pageable);
        return ResponseEntity.ok(CustomApiResponse.<Page<ClaimDto>>builder().data(claims).build());
    }

    @GetMapping("/my-claims")
    public ResponseEntity<CustomApiResponse<Page<ClaimDto>>> getMyClaims(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "claimDate,desc") String sort) {
        String employeeId = resolveEmployeeId(userDetails);
        Sort order = parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, order);
        Page<ClaimDto> claims = claimService.getByEmployee(employeeId, pageable);
        return ResponseEntity.ok(CustomApiResponse.<Page<ClaimDto>>builder().data(claims).build());
    }

    @GetMapping
    public ResponseEntity<CustomApiResponse<Page<ClaimDto>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "claimDate,desc") String sort) {
        Sort order = parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, order);
        Page<ClaimDto> claims = claimService.getAll(pageable);
        return ResponseEntity.ok(CustomApiResponse.<Page<ClaimDto>>builder().data(claims).build());
    }

    @GetMapping("/pending")
    public ResponseEntity<CustomApiResponse<Page<ClaimDto>>> getPending(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "claimDate,desc") String sort) {
        Sort order = parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, order);
        Page<ClaimDto> claims = claimService.getPending(pageable);
        return ResponseEntity.ok(CustomApiResponse.<Page<ClaimDto>>builder().data(claims).build());
    }

    @PostMapping("/{id}/attachments")
    public ResponseEntity<CustomApiResponse<com.hrms.dto.ClaimAttachmentDto>> addAttachment(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "attachmentType", defaultValue = "RECEIPT") String attachmentType) throws IOException {
        com.hrms.dto.ClaimAttachmentDto dto = claimAttachmentService.addAttachment(id, file, attachmentType);
        return new ResponseEntity<>(CustomApiResponse.<com.hrms.dto.ClaimAttachmentDto>builder().data(dto).build(), HttpStatus.CREATED);
    }

    @GetMapping("/{id}/attachments")
    public ResponseEntity<CustomApiResponse<List<com.hrms.dto.ClaimAttachmentDto>>> getAttachments(@PathVariable Long id) {
        List<com.hrms.dto.ClaimAttachmentDto> attachments = claimAttachmentService.getAttachments(id);
        return ResponseEntity.ok(CustomApiResponse.<List<com.hrms.dto.ClaimAttachmentDto>>builder().data(attachments).build());
    }

    @DeleteMapping("/{id}/attachments/{attachmentId}")
    public ResponseEntity<CustomApiResponse<Void>> deleteAttachment(
            @PathVariable Long id,
            @PathVariable Long attachmentId) throws IOException {
        claimAttachmentService.deleteAttachment(id, attachmentId);
        return ResponseEntity.ok(CustomApiResponse.<Void>builder().returnMessage("Attachment deleted").build());
    }

    @GetMapping("/{id}/attachments/{attachmentId}/download")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable Long id, @PathVariable Long attachmentId) throws IOException {
        com.hrms.model.ClaimAttachment att = claimAttachmentService.getAttachment(id, attachmentId);
        byte[] content = claimAttachmentService.getAttachmentContent(id, attachmentId);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + att.getFileName() + "\"")
                .body(content);
    }

    private String resolveEmployeeId(UserDetailsImpl userDetails) {
        Employee emp = employeeRepository.findByUser_UserId(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for current user"));
        return emp.getEmployeeId();
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) return Sort.by(Sort.Direction.DESC, "claimDate");
        String[] parts = sort.split(",");
        String property = parts[0].trim();
        Sort.Direction dir = parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(dir, property);
    }
}
