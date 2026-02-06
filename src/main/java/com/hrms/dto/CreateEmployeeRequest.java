package com.hrms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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
    private String username;

    @NotBlank
    private String password;

    @NotNull
    private Integer departmentId;

    private Long positionId;

    private String position;

    @NotNull
    private String role;
}
