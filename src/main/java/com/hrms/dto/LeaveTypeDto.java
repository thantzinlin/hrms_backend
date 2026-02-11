package com.hrms.dto;

import lombok.Data;

@Data
public class LeaveTypeDto {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer maxDays;
    private Boolean isActive;
}
