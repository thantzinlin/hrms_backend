package com.hrms.service;

import com.hrms.dto.CreateEmployeeRequest;
import com.hrms.dto.EmployeeDto;
import com.hrms.dto.UpdateEmployeeRequest;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.model.*;
import com.hrms.repository.DepartmentRepository;
import com.hrms.repository.EmployeeRepository;
import com.hrms.repository.PositionRepository;
import com.hrms.repository.RoleRepository;
import com.hrms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${userIdPrefix}")
    private String userIdPrefix;

    @Value("${employeeIdPrefix}")
    private String employeeIdPrefix;

    @Transactional
    public EmployeeDto createEmployee(CreateEmployeeRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Error: Email is already in use!");
        }

        User user = new User(request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()));
        user.setUserId(generateNextUserId());

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByRoleName(request.getRole())
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);

        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with id: " + request.getDepartmentId()));

        Position position = resolvePosition(request.getPositionId(), request.getPosition());

        Employee employee = new Employee();
        employee.setEmployeeId(this.generateNextEmployeeId());
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());

        employee.setJoinDate(request.getJoinDate());
        employee.setStatus(request.getStatus());
        employee.setUser(savedUser);
        employee.setDepartment(department);
        employee.setJobPosition(position);

        Employee savedEmployee = employeeRepository.save(employee);
        return mapToDto(savedEmployee);
    }

    public Page<EmployeeDto> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable).map(this::mapToDto);
    }

    public EmployeeDto getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        return mapToDto(employee);
    }

    @Transactional
    public EmployeeDto updateEmployee(Long id, UpdateEmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        if (request.getName() != null) {
            employee.setName(request.getName());
        }
        if (request.getEmail() != null) {
            employee.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            employee.setPhone(request.getPhone());
        }
        if (request.getJoinDate() != null) {
            employee.setJoinDate(request.getJoinDate());
        }
        if (request.getStatus() != null) {
            employee.setStatus(request.getStatus());
        }
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Department not found with id: " + request.getDepartmentId()));
            employee.setDepartment(department);
        }
        if (request.getPositionId() != null || (request.getPosition() != null && !request.getPosition().isBlank())) {
            Position position = resolvePosition(request.getPositionId(), request.getPosition());
            employee.setJobPosition(position);
        }
        if (request.getRoleName() != null && !request.getRoleName().isBlank()) {
            User user = employee.getUser();
            if (user != null) {
                Role userRole = roleRepository.findByRoleName(request.getRoleName())
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                Set<Role> roles = new HashSet<>();
                roles.add(userRole);
                user.setRoles(roles);
                userRepository.save(user);
            }
        }

        Employee updatedEmployee = employeeRepository.save(employee);
        return mapToDto(updatedEmployee);
    }

    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        employeeRepository.delete(employee);
        userRepository.delete(employee.getUser());
    }

    private EmployeeDto mapToDto(Employee employee) {
        EmployeeDto dto = new EmployeeDto();
        dto.setId(employee.getId());
        dto.setEmployeeId(employee.getEmployeeId());
        dto.setName(employee.getName());
        dto.setEmail(employee.getEmail());
        dto.setPhone(employee.getPhone());
        dto.setJoinDate(employee.getJoinDate().toString());
        dto.setStatus(employee.getStatus());
        if (employee.getUser() != null) {
            dto.setUserId(employee.getUser().getUserId());
            if (employee.getUser().getRoles() != null && !employee.getUser().getRoles().isEmpty()) {
                String roleName = employee.getUser().getRoles().iterator().next().getRoleName();
                dto.setRole(roleName);
            }
        }
        if (employee.getDepartment() != null) {
            dto.setDepartmentId(employee.getDepartment().getId());
            dto.setDepartmentName(employee.getDepartment().getName());
        }
        if (employee.getJobPosition() != null) {
            dto.setPositionId(employee.getJobPosition().getPositionId());
            dto.setPosition(employee.getJobPosition().getPositionName());
        }
        return dto;
    }

    private com.hrms.model.Position resolvePosition(Long positionId, String positionName) {
        if (positionId != null) {
            return positionRepository.findById(positionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Position not found with id: " + positionId));
        }
        if (positionName != null && !positionName.isBlank()) {
            return positionRepository.findByPositionName(positionName)
                    .orElseThrow(() -> new ResourceNotFoundException("Position not found with name: " + positionName));
        }
        return null;
    }

    private String generateNextEmployeeId() {
        String lastId = employeeRepository.findMaxEmployeeId();
        String prefix = employeeIdPrefix;
        if (lastId == null) {
            return prefix + "00001";
        }

        int numeric = Integer.parseInt(lastId.substring(prefix.length()));
        int next = numeric + 1;

        return prefix + String.format("%05d", next);
    }

    private String generateNextUserId() {
        String lastId = userRepository.findMaxUserId();

        if (lastId == null) {
            return userIdPrefix + "00001";
        }

        int numeric = Integer.parseInt(lastId.substring(userIdPrefix.length()));
        int next = numeric + 1;

        return userIdPrefix + String.format("%05d", next);
    }

}
