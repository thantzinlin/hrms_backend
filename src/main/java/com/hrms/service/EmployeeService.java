package com.hrms.service;

import com.hrms.dto.CreateEmployeeRequest;
import com.hrms.dto.EmployeeDto;
import com.hrms.dto.UpdateEmployeeRequest;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.model.*;
import com.hrms.repository.ApprovalAuthorityRepository;
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

import java.time.LocalDate;
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
    private ApprovalAuthorityRepository approvalAuthorityRepository;

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
        employee.setFatherName(request.getFatherName());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setNationality(request.getNationality());
        employee.setRace(request.getRace());
        employee.setGender(request.getGender());
        employee.setMaritalStatus(request.getMaritalStatus());
        employee.setNrc(request.getNrc());
        employee.setUser(savedUser);
        employee.setDepartment(department);
        employee.setJobPosition(position);
        if (request.getReportingToId() != null) {
            Employee manager = employeeRepository.findById(request.getReportingToId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Reporting manager not found with id: " + request.getReportingToId()));
            employee.setReportingTo(manager);
            ensureManagerHasSupervisorAuthority(manager);
        }

        Employee savedEmployee = employeeRepository.save(employee);
        saveApprovalAuthority(savedEmployee,
                request.getCanApproveLeave(),
                request.getCanApproveOvertime(),
                request.getIsHr());
        EmployeeDto dto = mapToDto(savedEmployee);
        enrichDtoWithApprovalAuthority(dto, savedEmployee.getId());
        return dto;
    }

    public Page<EmployeeDto> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable).map(this::mapToDto);
    }

    public Page<EmployeeDto> searchEmployees(String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return employeeRepository.searchByEmployeeIdOrNameOrEmailOrPhone(search.trim(), pageable).map(this::mapToDto);
        }
        return getAllEmployees(pageable);
    }

    public EmployeeDto getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        EmployeeDto dto = mapToDto(employee);
        enrichDtoWithApprovalAuthority(dto, id);
        return dto;
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
        if (request.getFatherName() != null) {
            employee.setFatherName(request.getFatherName());
        }
        if (request.getDateOfBirth() != null) {
            employee.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getNationality() != null) {
            employee.setNationality(request.getNationality());
        }
        if (request.getRace() != null) {
            employee.setRace(request.getRace());
        }
        if (request.getGender() != null) {
            employee.setGender(request.getGender());
        }
        if (request.getMaritalStatus() != null) {
            employee.setMaritalStatus(request.getMaritalStatus());
        }
        if (request.getNrc() != null) {
            employee.setNrc(request.getNrc());
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
        if (request.getReportingToId() != null) {
            Employee manager = employeeRepository.findById(request.getReportingToId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Reporting manager not found with id: " + request.getReportingToId()));
            employee.setReportingTo(manager);
            ensureManagerHasSupervisorAuthority(manager);
        } else {
            employee.setReportingTo(null);
        }

        Employee updatedEmployee = employeeRepository.save(employee);
        saveApprovalAuthority(updatedEmployee,
                request.getCanApproveLeave(),
                request.getCanApproveOvertime(),
                request.getIsHr());
        EmployeeDto dto = mapToDto(updatedEmployee);
        enrichDtoWithApprovalAuthority(dto, updatedEmployee.getId());
        return dto;
    }

    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        approvalAuthorityRepository.findByEmployee_Id(id).ifPresent(approvalAuthorityRepository::delete);
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
        dto.setFatherName(employee.getFatherName());
        dto.setDateOfBirth(employee.getDateOfBirth() != null ? employee.getDateOfBirth().toString() : null);
        dto.setNationality(employee.getNationality());
        dto.setRace(employee.getRace());
        dto.setGender(employee.getGender());
        dto.setMaritalStatus(employee.getMaritalStatus());
        dto.setNrc(employee.getNrc());
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
        if (employee.getReportingTo() != null) {
            dto.setReportingToId(employee.getReportingTo().getId());
            dto.setReportingToName(employee.getReportingTo().getName());
        }
        return dto;
    }

    /** Load approval_authorities for this employee and set flags on DTO. */
    private void enrichDtoWithApprovalAuthority(EmployeeDto dto, Long employeeId) {
        approvalAuthorityRepository.findByEmployee_Id(employeeId).ifPresent(auth -> {
            dto.setCanApproveLeave(auth.getCanApproveLeave());
            dto.setCanApproveOvertime(auth.getCanApproveOvertime());
            dto.setIsHr(auth.getIsHr());
        });
    }

    /** Create or update approval_authorities for this employee. */
    private void saveApprovalAuthority(Employee employee, Boolean canApproveLeave, Boolean canApproveOvertime, Boolean isHr) {
        boolean hasAny = Boolean.TRUE.equals(canApproveLeave) || Boolean.TRUE.equals(canApproveOvertime) || Boolean.TRUE.equals(isHr);
        ApprovalAuthority auth = approvalAuthorityRepository.findByEmployee_Id(employee.getId()).orElse(null);
        if (hasAny) {
            if (auth == null) {
                auth = new ApprovalAuthority();
                auth.setEmployee(employee);
            }
            auth.setCanApproveLeave(Boolean.TRUE.equals(canApproveLeave));
            auth.setCanApproveOvertime(Boolean.TRUE.equals(canApproveOvertime));
            auth.setIsHr(Boolean.TRUE.equals(isHr));
            approvalAuthorityRepository.save(auth);
        } else if (auth != null) {
            approvalAuthorityRepository.delete(auth);
        }
    }

    /**
     * When an employee is assigned a "Reports to" manager, ensure that manager has
     * supervisor approval authority so they appear in GET /approvals/pending.
     * If the manager has no approval_authorities row yet, create one with
     * can_approve_leave and can_approve_overtime = true (is_hr unchanged/false).
     * If they already have a row, do not override their existing flags.
     */
    private void ensureManagerHasSupervisorAuthority(Employee manager) {
        if (manager == null) return;
        ApprovalAuthority auth = approvalAuthorityRepository.findByEmployee_Id(manager.getId()).orElse(null);
        if (auth == null) {
            auth = new ApprovalAuthority();
            auth.setEmployee(manager);
            auth.setCanApproveLeave(true);
            auth.setCanApproveOvertime(true);
            auth.setIsHr(false);
            approvalAuthorityRepository.save(auth);
        } else {
            if (!Boolean.TRUE.equals(auth.getCanApproveLeave())) auth.setCanApproveLeave(true);
            if (!Boolean.TRUE.equals(auth.getCanApproveOvertime())) auth.setCanApproveOvertime(true);
            approvalAuthorityRepository.save(auth);
        }
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
