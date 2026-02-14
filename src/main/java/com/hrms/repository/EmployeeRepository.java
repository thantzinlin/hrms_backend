package com.hrms.repository;

import com.hrms.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmployeeId(String employeeId);

    Optional<Employee> findByUser_UserId(String userId);

    List<Employee> findByUser_UserIdIn(Collection<String> userIds);

    @Query("SELECT MAX(e.employeeId) FROM Employee e")
    String findMaxEmployeeId();

    @Query("SELECT e FROM Employee e WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(e.employeeId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "(e.phone IS NOT NULL AND LOWER(e.phone) LIKE LOWER(CONCAT('%', :search, '%'))))")
    Page<Employee> searchByEmployeeIdOrNameOrEmailOrPhone(@Param("search") String search, Pageable pageable);

    List<Employee> findByDepartment_Id(Integer departmentId);

}
