package com.hrms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HierarchyNodeDto {
    private Long id;
    private String employeeId;
    private String name;
    private String email;
    /** Display label for hierarchy (e.g. position name). */
    private String positionName;
    private Long reportingToId;
    private List<HierarchyNodeDto> subordinates;
}
