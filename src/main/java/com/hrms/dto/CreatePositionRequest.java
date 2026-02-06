package com.hrms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePositionRequest {

    @NotBlank(message = "Position name is required")
    private String positionName;

    private String description;

    private Boolean isActive = true;
}
