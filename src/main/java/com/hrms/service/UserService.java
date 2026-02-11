package com.hrms.service;

import com.hrms.dto.UserDto;
import com.hrms.model.Employee;
import com.hrms.model.Role;
import com.hrms.model.User;
import com.hrms.repository.EmployeeRepository;
import com.hrms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public Page<UserDto> searchUsers(String search, Pageable pageable) {
        Page<User> userPage = (search != null && !search.isBlank())
                ? userRepository.searchUsers(search.trim(), pageable)
                : userRepository.findByIsDeletedFalse(pageable);

        List<String> userIds = userPage.getContent().stream().map(User::getUserId).toList();
        List<Employee> employees = employeeRepository.findByUser_UserIdIn(userIds);
        Map<String, Employee> empByUserId = employees.stream()
                .filter(e -> e.getUser() != null)
                .collect(Collectors.toMap(e -> e.getUser().getUserId(), e -> e, (a, b) -> a));

        return userPage.map(u -> mapToDto(u, empByUserId.get(u.getUserId())));
    }

    private UserDto mapToDto(User user, Employee employee) {
        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());
        dto.setRoles(roleNames);
        if (employee != null) {
            dto.setEmployeeName(employee.getName());
            dto.setPhone(employee.getPhone());
        }
        return dto;
    }
}
