package com.hrms.repository;

import com.hrms.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmployeeId(String employeeId);

    Optional<Employee> findByUser_UserId(String userId);

    @Query("SELECT MAX(e.employeeId) FROM Employee e")
    String findMaxEmployeeId();

}
