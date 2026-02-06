package com.hrms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateMenuRequest {
    @NotBlank(message = "Menu name is required")
    private String menuName;

    @NotBlank(message = "Module code is required")
    private String moduleCode;

    private Long parentId;
    private String url;
    private String icon;
    private Integer sequence = 0;
}
