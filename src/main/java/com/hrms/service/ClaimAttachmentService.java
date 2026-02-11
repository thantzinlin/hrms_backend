package com.hrms.service;

import com.hrms.dto.ClaimAttachmentDto;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.model.Claim;
import com.hrms.model.ClaimAttachment;
import com.hrms.repository.ClaimAttachmentRepository;
import com.hrms.repository.ClaimRepository;
import com.hrms.config.FileStorageConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClaimAttachmentService {

    private static final String[] ALLOWED_CONTENT_TYPES = {
            "application/pdf", "image/jpeg", "image/png", "image/gif",
            "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    };

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private ClaimAttachmentRepository attachmentRepository;

    @Autowired
    private FileStorageConfig fileStorageConfig;

    @Transactional
    public ClaimAttachmentDto addAttachment(Long claimId, MultipartFile file, String attachmentType) throws IOException {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + claimId));

        if (claim.getStatus() != com.hrms.model.ClaimStatus.DRAFT) {
            throw new IllegalStateException("Attachments can only be added to draft claims.");
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        String contentType = file.getContentType();
        if (contentType != null && !isAllowedContentType(contentType)) {
            throw new IllegalArgumentException("File type not allowed. Allowed: PDF, JPEG, PNG, GIF, DOC, DOCX");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            originalName = "attachment";
        }

        String extension = getExtension(originalName);
        String storedName = UUID.randomUUID().toString() + extension;
        Path targetPath = fileStorageConfig.getUploadPath().resolve(storedName);
        Files.copy(file.getInputStream(), targetPath);

        ClaimAttachment attachment = new ClaimAttachment();
        attachment.setClaim(claim);
        attachment.setFileName(sanitizeFileName(originalName));
        attachment.setFilePath(targetPath.toString());
        attachment.setFileType(file.getContentType());
        attachment.setFileSize(file.getSize());
        attachment.setAttachmentType(attachmentType != null ? attachmentType : "RECEIPT");

        ClaimAttachment saved = attachmentRepository.save(attachment);
        return mapToDto(saved);
    }

    public List<ClaimAttachmentDto> getAttachments(Long claimId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + claimId));
        return attachmentRepository.findByClaim(claim).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAttachment(Long claimId, Long attachmentId) throws IOException {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + claimId));

        if (claim.getStatus() != com.hrms.model.ClaimStatus.DRAFT) {
            throw new IllegalStateException("Attachments can only be removed from draft claims.");
        }

        ClaimAttachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id: " + attachmentId));

        if (!attachment.getClaim().getId().equals(claimId)) {
            throw new IllegalArgumentException("Attachment does not belong to this claim");
        }

        Path path = Path.of(attachment.getFilePath());
        if (Files.exists(path)) {
            Files.delete(path);
        }
        attachmentRepository.delete(attachment);
    }

    public ClaimAttachment getAttachment(Long claimId, Long attachmentId) {
        ClaimAttachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id: " + attachmentId));

        if (!attachment.getClaim().getId().equals(claimId)) {
            throw new IllegalArgumentException("Attachment does not belong to this claim");
        }
        return attachment;
    }

    public byte[] getAttachmentContent(Long claimId, Long attachmentId) throws IOException {
        ClaimAttachment attachment = getAttachment(claimId, attachmentId);

        Path path = Path.of(attachment.getFilePath());
        if (!Files.exists(path)) {
            throw new ResourceNotFoundException("File not found on disk");
        }
        return Files.readAllBytes(path);
    }

    private boolean isAllowedContentType(String contentType) {
        for (String allowed : ALLOWED_CONTENT_TYPES) {
            if (allowed.equalsIgnoreCase(contentType)) return true;
        }
        return false;
    }

    private String getExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot > 0 ? fileName.substring(dot) : "";
    }

    private String sanitizeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private ClaimAttachmentDto mapToDto(ClaimAttachment a) {
        ClaimAttachmentDto dto = new ClaimAttachmentDto();
        dto.setId(a.getId());
        dto.setFileName(a.getFileName());
        dto.setFileType(a.getFileType());
        dto.setFileSize(a.getFileSize());
        dto.setAttachmentType(a.getAttachmentType());
        return dto;
    }
}
