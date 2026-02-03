package com.hrms.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hrms.model.User;
import com.hrms.security.services.UserDetailsImpl;
import com.hrms.service.UserInfoService;
import com.hrms.service.MenuService;
import com.hrms.dto.MenuDTO;

import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;
    private final UserInfoService userInfoService;

    public MenuController(MenuService menuService, UserInfoService userInfoService) {
        this.menuService = menuService;
        this.userInfoService = userInfoService;
    }

    @GetMapping()
    public List<MenuDTO> getMenus(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userInfoService.findByUserId(userDetails.getUserId());
        return menuService.getMenusByRoles(
                user);
    }
}
