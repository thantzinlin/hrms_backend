package com.hrms.dto;

import lombok.Data;

@Data
public class ClaimAttachmentDto {
    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String attachmentType;
    /** Download URL path - not stored in DB, built by controller. */
    private String downloadUrl;
}
