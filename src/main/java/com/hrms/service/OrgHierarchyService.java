package com.hrms.service;

import com.hrms.dto.HierarchyNodeDto;
import com.hrms.model.Employee;
import com.hrms.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrgHierarchyService {

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Build reporting hierarchy tree. Roots are employees with no reporting_to.
     */
    public List<HierarchyNodeDto> getHierarchy() {
        List<Employee> all = employeeRepository.findAll();
        Map<Long, List<Employee>> byManager = all.stream()
                .filter(e -> e.getReportingTo() != null)
                .collect(Collectors.groupingBy(e -> e.getReportingTo().getId()));
        List<Employee> roots = all.stream()
                .filter(e -> e.getReportingTo() == null)
                .collect(Collectors.toList());
        List<HierarchyNodeDto> result = new ArrayList<>();
        for (Employee root : roots) {
            result.add(buildNode(root, byManager));
        }
        return result;
    }

    private HierarchyNodeDto buildNode(Employee e, Map<Long, List<Employee>> byManager) {
        List<Employee> subs = byManager.getOrDefault(e.getId(), List.of());
        List<HierarchyNodeDto> subDtos = subs.stream()
                .map(sub -> buildNode(sub, byManager))
                .collect(Collectors.toList());
        return HierarchyNodeDto.builder()
                .id(e.getId())
                .employeeId(e.getEmployeeId())
                .name(e.getName())
                .email(e.getEmail())
                .reportingToId(e.getReportingTo() != null ? e.getReportingTo().getId() : null)
                .subordinates(subDtos)
                .build();
    }
}
