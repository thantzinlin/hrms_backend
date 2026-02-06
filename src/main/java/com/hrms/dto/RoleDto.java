package com.hrms.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleDto {
    private Long roleId;
    private String roleName;
    private String description;
    private List<MenuDTO> menus;
}
