package com.hrms.controller;

import com.hrms.dto.HierarchyNodeDto;
import com.hrms.service.OrgHierarchyService;
import com.hrms.util.CustomApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/org")
public class OrgController {

    @Autowired
    private OrgHierarchyService orgHierarchyService;

    @GetMapping("/hierarchy")
    public ResponseEntity<CustomApiResponse<List<HierarchyNodeDto>>> getHierarchy() {
        List<HierarchyNodeDto> hierarchy = orgHierarchyService.getHierarchy();
        return ResponseEntity.ok(CustomApiResponse.<List<HierarchyNodeDto>>builder().data(hierarchy).build());
    }
}
