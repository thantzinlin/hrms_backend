package com.hrms.util;

import com.hrms.model.Role;
import com.hrms.model.RoleName;
import com.hrms.model.User;
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
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // String[] defaultRoles = {
        // "ADMIN",
        // "HR",
        // "MANAGER",
        // "EMPLOYEE"
        // };

        // for (String roleName : defaultRoles) {
        // if (roleRepository.findByRoleName(roleName).isEmpty()) {
        // Role role = new Role();
        // role.setRoleName(roleName);

        // role.setIsDeleted(false);
        // role.setCreatedBy("system");
        // role.setUpdateBy("system");
        // role.setCreatedAt(LocalDateTime.now());
        // role.setUpdatedAt(LocalDateTime.now());
        // role.setVersion(0);

        // roleRepository.save(role);
        // System.out.println("Inserted default role: " + roleName);
        // }
        // }

        // if (!userRepository.existsByUsername("admin")) {
        // Role adminRole = roleRepository.findByRoleName("ADMIN")
        // .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        // User adminUser = new User("admin", "admin@hrms.com",
        // passwordEncoder.encode("password"));
        // adminUser.setUserId("admin-user-id"); // Manually set userId
        // adminUser.setRoles(Set.of(adminRole));
        // userRepository.save(adminUser);
        // }
    }
}
