package com.hrms.dto;

import lombok.Data;

import java.util.List;

@Data
public class MenuAdminDto {
    private Long menuId;
    private String menuName;
    private String moduleCode;
    private Long parentId;
    private String url;
    private String icon;
    private Integer sequence;
    private List<MenuAdminDto> children;
}
