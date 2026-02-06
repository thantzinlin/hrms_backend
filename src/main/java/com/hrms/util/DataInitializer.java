package com.hrms.util;

import com.hrms.model.Employee;
import com.hrms.model.Role;
import com.hrms.model.RoleName;
import com.hrms.model.User;
import com.hrms.repository.EmployeeRepository;
import com.hrms.repository.RoleRepository;
import com.hrms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String[] defaultRoles = {
                "ADMIN"
                // "HR",
                // "MANAGER",
                // "EMPLOYEE"
        };

        for (String roleName : defaultRoles) {
            if (roleRepository.findByRoleName(roleName).isEmpty()) {
                Role role = new Role();
                role.setRoleName(roleName);

                role.setIsDeleted(false);
                role.setCreatedBy("system");
                role.setUpdateBy("system");
                role.setCreatedAt(LocalDateTime.now());
                role.setUpdatedAt(LocalDateTime.now());
                role.setVersion(0);

                roleRepository.save(role);
                System.out.println("Inserted default role: " + roleName);
            }
        }

        if (!userRepository.existsByUsername("admin")) {
            Role adminRole = roleRepository.findByRoleName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            User adminUser = new User("admin", "admin@gmail.com",
                    passwordEncoder.encode("1qaz!QAZ"));
            adminUser.setUserId("USR00001"); // Manually set userId
            adminUser.setRoles(Set.of(adminRole));
            userRepository.save(adminUser);

            // insert into user_role

            // insert employee
            if (!employeeRepository.findByEmployeeId("EMP00001").isPresent()) {
                Employee employee = new Employee();
                employee.setEmployeeId("EMP00001");
                employee.setName("Thant Zin Lin");
                employee.setEmail("admin@gmail.com");
                employee.setPhone("1234567890");
                employee.setJoinDate(LocalDateTime.now());
                employee.setStatus("ACTIVE");
                employee.setCreatedBy("system");
                employee.setUpdateBy("system");
                employee.setCreatedAt(LocalDateTime.now());
                employee.setUpdatedAt(LocalDateTime.now());
                employee.setVersion(0);
                employee.setUser(adminUser);
                employeeRepository.save(employee);

            }
        }

    }
}
