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
    private String userId;
    private String role;
    private Integer departmentId;
    private String departmentName;
    private Long positionId;
    private String position;
}
