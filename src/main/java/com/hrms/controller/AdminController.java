package com.hrms.controller;

import com.hrms.dto.CreateDepartmentRequest;
import com.hrms.dto.CreateHolidayRequest;
import com.hrms.dto.DepartmentDto;
import com.hrms.dto.HolidayDto;
import com.hrms.service.DepartmentService;
import com.hrms.service.HolidayService;
import com.hrms.util.CustomApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private HolidayService holidayService;

    // Department Endpoints
    @PostMapping("/departments")
    public ResponseEntity<CustomApiResponse<DepartmentDto>> createDepartment(@Valid @RequestBody CreateDepartmentRequest request) {
        DepartmentDto createdDepartment = departmentService.createDepartment(request);
        return new ResponseEntity<>(CustomApiResponse.<DepartmentDto>builder().data(createdDepartment).build(), HttpStatus.CREATED);
    }

    @GetMapping("/departments")
    public ResponseEntity<CustomApiResponse<List<DepartmentDto>>> getAllDepartments() {
        List<DepartmentDto> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(CustomApiResponse.<List<DepartmentDto>>builder().data(departments).build());
    }

    @GetMapping("/departments/{id}")
    public ResponseEntity<CustomApiResponse<DepartmentDto>> getDepartmentById(@PathVariable Integer id) {
        DepartmentDto department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(CustomApiResponse.<DepartmentDto>builder().data(department).build());
    }

    @PutMapping("/departments/{id}")
    public ResponseEntity<CustomApiResponse<DepartmentDto>> updateDepartment(@PathVariable Integer id,
            @Valid @RequestBody CreateDepartmentRequest request) {
        DepartmentDto updatedDepartment = departmentService.updateDepartment(id, request);
        return ResponseEntity.ok(CustomApiResponse.<DepartmentDto>builder().data(updatedDepartment).build());
    }

    @DeleteMapping("/departments/{id}")
    public ResponseEntity<CustomApiResponse<Void>> deleteDepartment(@PathVariable Integer id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(CustomApiResponse.<Void>builder().data(null).returnMessage("Deleted successfully").build());
    }

    // Holiday Endpoints
    @PostMapping("/holidays")
    public ResponseEntity<CustomApiResponse<HolidayDto>> createHoliday(@Valid @RequestBody CreateHolidayRequest request) {
        HolidayDto createdHoliday = holidayService.createHoliday(request);
        return new ResponseEntity<>(CustomApiResponse.<HolidayDto>builder().data(createdHoliday).build(), HttpStatus.CREATED);
    }

    @GetMapping("/holidays")
    public ResponseEntity<CustomApiResponse<List<HolidayDto>>> getAllHolidays() {
        List<HolidayDto> holidays = holidayService.getAllHolidays();
        return ResponseEntity.ok(CustomApiResponse.<List<HolidayDto>>builder().data(holidays).build());
    }

    @GetMapping("/holidays/{id}")
    public ResponseEntity<CustomApiResponse<HolidayDto>> getHolidayById(@PathVariable Integer id) {
        HolidayDto holiday = holidayService.getHolidayById(id);
        return ResponseEntity.ok(CustomApiResponse.<HolidayDto>builder().data(holiday).build());
    }

    @PutMapping("/holidays/{id}")
    public ResponseEntity<CustomApiResponse<HolidayDto>> updateHoliday(@PathVariable Integer id,
            @Valid @RequestBody CreateHolidayRequest request) {
        HolidayDto updatedHoliday = holidayService.updateHoliday(id, request);
        return ResponseEntity.ok(CustomApiResponse.<HolidayDto>builder().data(updatedHoliday).build());
    }

    @DeleteMapping("/holidays/{id}")
    public ResponseEntity<CustomApiResponse<Void>> deleteHoliday(@PathVariable Integer id) {
        holidayService.deleteHoliday(id);
        return ResponseEntity.ok(CustomApiResponse.<Void>builder().data(null).returnMessage("Deleted successfully").build());
    }
}
