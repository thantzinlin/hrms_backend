package com.hrms.dto;

import lombok.Data;

import java.util.List;

@Data
public class JwtResponse {

    private String accessToken;
    private String refreshToken;
    private String type = "Bearer";
    private String id;
    private String username;
    private String email;
    private List<String> roles;
    private String employeeId;

    public JwtResponse(String accessToken, String refreshToken, String id, String username, String email,
            List<String> roles, String employeeId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.employeeId = employeeId;
    }
}
