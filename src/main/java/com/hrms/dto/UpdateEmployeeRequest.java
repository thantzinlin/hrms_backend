package com.hrms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateEmployeeRequest {
    private String name;

    @Email
    private String email;

    private String phone;

    private LocalDateTime joinDate;

    private String status;

    private Integer departmentId;

    private Long positionId;

    private String position;

    @JsonProperty("role")
    private String roleName;

    /** Optional: reporting manager id (employees.id). Set to null to clear. */
    private Long reportingToId;

    /** Approval authority: can approve leave at supervisor level. */
    private Boolean canApproveLeave;
    /** Approval authority: can approve overtime at supervisor level. */
    private Boolean canApproveOvertime;
    /** Approval authority: is HR (final approval). */
    private Boolean isHr;
}
