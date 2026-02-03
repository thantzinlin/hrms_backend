package com.hrms.dto;

import java.util.List;

import lombok.Data;

@Data
public class MenuDTO {
    private Long menuId;
    private String menuName;
    private String url;
    private String icon;
    private Integer sequence;
    private List<MenuDTO> children;
}
