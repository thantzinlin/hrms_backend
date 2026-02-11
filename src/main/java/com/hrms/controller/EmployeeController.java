package com.hrms.controller;

import com.hrms.dto.CreateEmployeeRequest;
import com.hrms.dto.EmployeeDto;
import com.hrms.dto.UpdateEmployeeRequest;
import com.hrms.service.EmployeeService;
import com.hrms.util.CustomApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    // @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<CustomApiResponse<EmployeeDto>> createEmployee(
            @Valid @RequestBody CreateEmployeeRequest request) {
        EmployeeDto createdEmployee = employeeService.createEmployee(request);
        return new ResponseEntity<>(CustomApiResponse.<EmployeeDto>builder().data(createdEmployee).build(),
                HttpStatus.CREATED);
    }

    @GetMapping
    // @PreAuthorize("hasRole('ADMIN') or hasRole('HR') or hasRole('MANAGER')")
    public ResponseEntity<CustomApiResponse<Page<EmployeeDto>>> getAllEmployees(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        Page<EmployeeDto> employees = employeeService.searchEmployees(search, pageable);
        return ResponseEntity.ok(CustomApiResponse.<Page<EmployeeDto>>builder().data(employees).build());
    }

    @GetMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('HR') or hasRole('MANAGER')")
    public ResponseEntity<CustomApiResponse<EmployeeDto>> getEmployeeById(@PathVariable Long id) {
        EmployeeDto employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(CustomApiResponse.<EmployeeDto>builder().data(employee).build());
    }

    @PutMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<CustomApiResponse<EmployeeDto>> updateEmployee(@PathVariable Long id,
            @Valid @RequestBody UpdateEmployeeRequest request) {
        EmployeeDto updatedEmployee = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(CustomApiResponse.<EmployeeDto>builder().data(updatedEmployee).build());
    }

    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity<CustomApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity
                .ok(CustomApiResponse.<Void>builder().data(null).returnMessage("Deleted successfully").build());
    }
}
