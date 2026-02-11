package com.hrms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CreateEmployeeRequest {
    private String employeeId;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    private String phone;

    @NotNull
    private LocalDateTime joinDate;

    @NotBlank
    private String status;

    @NotBlank
    private String fatherName;
    @NotNull
    private LocalDate dateOfBirth;
    @NotBlank
    private String nationality;
    @NotBlank
    private String race;
    @NotBlank
    private String gender;
    @NotBlank
    private String maritalStatus;
    @NotBlank
    private String nrc;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotNull
    private Integer departmentId;

    private Long positionId;

    private String position;

    @NotNull
    private String role;

    /** Optional: reporting manager id (employees.id). */
    private Long reportingToId;

    /** Approval authority: can approve leave at supervisor level. */
    private Boolean canApproveLeave;
    /** Approval authority: can approve overtime at supervisor level. */
    private Boolean canApproveOvertime;
    /** Approval authority: is HR (final approval). */
    private Boolean isHr;
}
