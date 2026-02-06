package com.hrms.dto;

import lombok.Data;

@Data
public class PositionDto {
    private Long positionId;
    private String positionName;
    private String description;
    private Boolean isActive;
}
