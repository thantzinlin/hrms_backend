package com.hrms.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private String userId;
    private String username;
    private String email;
    private List<String> roles;
    private String employeeName;
    private String phone;
}
