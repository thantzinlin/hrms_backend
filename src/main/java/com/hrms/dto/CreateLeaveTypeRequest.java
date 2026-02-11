package com.hrms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateLeaveTypeRequest {
    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;
    private Integer maxDays;
    private Boolean isActive = true;
}
