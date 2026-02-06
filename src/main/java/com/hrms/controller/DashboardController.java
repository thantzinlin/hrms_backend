package com.hrms.controller;

import com.hrms.dto.DashboardStatsDto;
import com.hrms.service.DashboardService;
import com.hrms.util.CustomApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/dashboard")
// @PreAuthorize("hasRole('ADMIN') or hasRole('HR') or hasRole('MANAGER')")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<CustomApiResponse<DashboardStatsDto>> getDashboardStats() {
        DashboardStatsDto stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(CustomApiResponse.<DashboardStatsDto>builder().data(stats).build());
    }
}
