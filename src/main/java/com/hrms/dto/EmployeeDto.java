package com.hrms.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmployeeDto {
    private Long id;
    private String employeeId;
    private String name;
    private String email;
    private String phone;
    private LocalDateTime joinDate;
    private String status;
    private String userId;
    private Integer departmentId;
    private String departmentName;
    private String position;
}
