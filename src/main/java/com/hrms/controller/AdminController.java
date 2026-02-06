package com.hrms.controller;

import com.hrms.dto.CreateDepartmentRequest;
import com.hrms.dto.CreateHolidayRequest;
import com.hrms.dto.CreateMenuRequest;
import com.hrms.dto.CreateRoleRequest;
import com.hrms.dto.DepartmentDto;
import com.hrms.dto.HolidayDto;
import com.hrms.dto.MenuAdminDto;
import com.hrms.dto.MenuDTO;
import com.hrms.dto.RoleDto;
import com.hrms.dto.RoleMenuRequest;
import com.hrms.service.DepartmentService;
import com.hrms.service.HolidayService;
import com.hrms.service.MenuService;
import com.hrms.service.RoleService;
import com.hrms.util.CustomApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
// @PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MenuService menuService;

    // Department Endpoints
    @PostMapping("/departments")
    public ResponseEntity<CustomApiResponse<DepartmentDto>> createDepartment(
            @Valid @RequestBody CreateDepartmentRequest request) {
        DepartmentDto createdDepartment = departmentService.createDepartment(request);
        return new ResponseEntity<>(CustomApiResponse.<DepartmentDto>builder().data(createdDepartment).build(),
                HttpStatus.CREATED);
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
        return ResponseEntity
                .ok(CustomApiResponse.<Void>builder().data(null).returnMessage("Deleted successfully").build());
    }

    // Holiday Endpoints
    @PostMapping("/holidays")
    public ResponseEntity<CustomApiResponse<HolidayDto>> createHoliday(
            @Valid @RequestBody CreateHolidayRequest request) {
        HolidayDto createdHoliday = holidayService.createHoliday(request);
        return new ResponseEntity<>(CustomApiResponse.<HolidayDto>builder().data(createdHoliday).build(),
                HttpStatus.CREATED);
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
        return ResponseEntity
                .ok(CustomApiResponse.<Void>builder().data(null).returnMessage("Deleted successfully").build());
    }

    // Role Endpoints
    @PostMapping("/roles")
    public ResponseEntity<CustomApiResponse<RoleDto>> createRole(
            @Valid @RequestBody CreateRoleRequest request) {
        RoleDto createdRole = roleService.createRole(request);
        return new ResponseEntity<>(CustomApiResponse.<RoleDto>builder().data(createdRole).build(),
                HttpStatus.CREATED);
    }

    @GetMapping("/roles")
    public ResponseEntity<CustomApiResponse<List<RoleDto>>> getAllRoles() {
        List<RoleDto> roles = roleService.getAllRoles();
        return ResponseEntity.ok(CustomApiResponse.<List<RoleDto>>builder().data(roles).build());
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<CustomApiResponse<RoleDto>> getRoleById(@PathVariable Long id) {
        RoleDto role = roleService.getRoleById(id);
        return ResponseEntity.ok(CustomApiResponse.<RoleDto>builder().data(role).build());
    }

    @PutMapping("/roles/{id}")
    public ResponseEntity<CustomApiResponse<RoleDto>> updateRole(@PathVariable Long id,
            @Valid @RequestBody CreateRoleRequest request) {
        RoleDto updatedRole = roleService.updateRole(id, request);
        return ResponseEntity.ok(CustomApiResponse.<RoleDto>builder().data(updatedRole).build());
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<CustomApiResponse<Void>> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity
                .ok(CustomApiResponse.<Void>builder().data(null).returnMessage("Deleted successfully").build());
    }

    // Role-Menu Mapping Endpoints
    @PutMapping("/roles/{roleId}/menus")
    public ResponseEntity<CustomApiResponse<RoleDto>> assignMenusToRole(@PathVariable Long roleId,
            @Valid @RequestBody RoleMenuRequest request) {
        RoleDto role = roleService.assignMenusToRole(roleId, request);
        return ResponseEntity.ok(CustomApiResponse.<RoleDto>builder().data(role).build());
    }

    @GetMapping("/roles/{roleId}/menus")
    public ResponseEntity<CustomApiResponse<List<MenuDTO>>> getMenusByRoleId(@PathVariable Long roleId) {
        List<MenuDTO> menus = roleService.getMenusByRoleId(roleId);
        return ResponseEntity.ok(CustomApiResponse.<List<MenuDTO>>builder().data(menus).build());
    }

    // Menu Endpoints
    @PostMapping("/menus")
    public ResponseEntity<CustomApiResponse<MenuAdminDto>> createMenu(
            @Valid @RequestBody CreateMenuRequest request) {
        MenuAdminDto createdMenu = menuService.createMenu(request);
        return new ResponseEntity<>(CustomApiResponse.<MenuAdminDto>builder().data(createdMenu).build(),
                HttpStatus.CREATED);
    }

    @GetMapping("/menus")
    public ResponseEntity<CustomApiResponse<List<MenuAdminDto>>> getAllMenus() {
        List<MenuAdminDto> menus = menuService.getAllMenusForAdmin();
        return ResponseEntity.ok(CustomApiResponse.<List<MenuAdminDto>>builder().data(menus).build());
    }

    @GetMapping("/menus/flat")
    public ResponseEntity<CustomApiResponse<List<MenuAdminDto>>> getAllMenusFlat() {
        List<MenuAdminDto> menus = menuService.getAllMenusFlat();
        return ResponseEntity.ok(CustomApiResponse.<List<MenuAdminDto>>builder().data(menus).build());
    }

    @GetMapping("/menus/{id}")
    public ResponseEntity<CustomApiResponse<MenuAdminDto>> getMenuById(@PathVariable Long id) {
        MenuAdminDto menu = menuService.getMenuById(id);
        return ResponseEntity.ok(CustomApiResponse.<MenuAdminDto>builder().data(menu).build());
    }

    @PutMapping("/menus/{id}")
    public ResponseEntity<CustomApiResponse<MenuAdminDto>> updateMenu(@PathVariable Long id,
            @Valid @RequestBody CreateMenuRequest request) {
        MenuAdminDto updatedMenu = menuService.updateMenu(id, request);
        return ResponseEntity.ok(CustomApiResponse.<MenuAdminDto>builder().data(updatedMenu).build());
    }

    @DeleteMapping("/menus/{id}")
    public ResponseEntity<CustomApiResponse<Void>> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return ResponseEntity
                .ok(CustomApiResponse.<Void>builder().data(null).returnMessage("Deleted successfully").build());
    }
}
