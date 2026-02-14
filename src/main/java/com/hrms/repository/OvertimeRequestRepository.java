package com.hrms.repository;

import com.hrms.model.Employee;
import com.hrms.model.OvertimeRequest;
import com.hrms.model.OvertimeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface OvertimeRequestRepository extends JpaRepository<OvertimeRequest, Long> {
    List<OvertimeRequest> findByEmployee(Employee employee);
    Page<OvertimeRequest> findByEmployee(Employee employee, Pageable pageable);
    List<OvertimeRequest> findByStatus(OvertimeStatus status);
    long countByStatus(OvertimeStatus status);
    List<OvertimeRequest> findByStatusIn(Collection<OvertimeStatus> statuses);
    Page<OvertimeRequest> findByStatusIn(Collection<OvertimeStatus> statuses, Pageable pageable);

    /** Fetch overtime requests with employee and employee.reportingTo so approval resolution works without lazy load. */
    @Query("SELECT or FROM OvertimeRequest or JOIN FETCH or.employee e LEFT JOIN FETCH e.reportingTo WHERE or.status = :status")
    List<OvertimeRequest> findByStatusWithEmployeeAndReportingTo(@Param("status") OvertimeStatus status);

    /** Check for duplicate overtime: same employee, same date, status in PENDING or APPROVED. */
    boolean existsByEmployeeAndDateAndStatusIn(Employee employee, LocalDate date, Collection<OvertimeStatus> statuses);

    List<OvertimeRequest> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT or FROM OvertimeRequest or JOIN FETCH or.employee e LEFT JOIN FETCH e.department WHERE or.date BETWEEN :startDate AND :endDate")
    List<OvertimeRequest> findByDateBetweenWithEmployee(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT or FROM OvertimeRequest or JOIN FETCH or.employee e LEFT JOIN FETCH e.department WHERE e.department.id = :departmentId AND or.date BETWEEN :startDate AND :endDate")
    List<OvertimeRequest> findByDepartmentAndDateBetween(@Param("departmentId") Integer departmentId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
