package com.hrms.dto;

import lombok.Data;

@Data
public class EmployeeDto {
    private Long id;
    private String employeeId;
    private String name;
    private String email;
    private String phone;
    private String joinDate;
    private String status;
    private String fatherName;
    private String dateOfBirth;
    private String nationality;
    private String race;
    private String gender;
    private String maritalStatus;
    private String nrc;
    private String userId;
    private String role;
    private Integer departmentId;
    private String departmentName;
    private Long positionId;
    private String position;
    /** Reporting manager id (for hierarchy / approval workflow). */
    private Long reportingToId;
    private String reportingToName;

    /** Approval authority: can approve leave at supervisor level. */
    private Boolean canApproveLeave;
    /** Approval authority: can approve overtime at supervisor level. */
    private Boolean canApproveOvertime;
    /** Approval authority: is HR (final approval for leave & overtime). */
    private Boolean isHr;
}
