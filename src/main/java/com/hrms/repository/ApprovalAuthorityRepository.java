package com.hrms.repository;

import com.hrms.model.ApprovalAuthority;
import com.hrms.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalAuthorityRepository extends JpaRepository<ApprovalAuthority, Long> {
    Optional<ApprovalAuthority> findByEmployee(Employee employee);

    Optional<ApprovalAuthority> findByEmployee_Id(Long employeeId);

    List<ApprovalAuthority> findByIsHrTrue();
}
