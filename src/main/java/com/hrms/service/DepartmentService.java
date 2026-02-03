package com.hrms.service;

import com.hrms.dto.CreateDepartmentRequest;
import com.hrms.dto.DepartmentDto;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.model.Department;
import com.hrms.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Transactional
    public DepartmentDto createDepartment(CreateDepartmentRequest request) {
        Department department = new Department();
        department.setName(request.getName());
        Department savedDepartment = departmentRepository.save(department);
        return mapToDto(savedDepartment);
    }

    public List<DepartmentDto> getAllDepartments() {
        return departmentRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public DepartmentDto getDepartmentById(Integer id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        return mapToDto(department);
    }

    @Transactional
    public DepartmentDto updateDepartment(Integer id, CreateDepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
        department.setName(request.getName());
        Department updatedDepartment = departmentRepository.save(department);
        return mapToDto(updatedDepartment);
    }

    public void deleteDepartment(Integer id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Department not found with id: " + id);
        }
        departmentRepository.deleteById(id);
    }

    private DepartmentDto mapToDto(Department department) {
        DepartmentDto dto = new DepartmentDto();
        dto.setId(department.getId());
        dto.setName(department.getName());
        return dto;
    }
}
