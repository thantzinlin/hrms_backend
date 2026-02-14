package com.hrms.repository;

import com.hrms.model.Employee;
import com.hrms.model.LeaveRequest;
import com.hrms.model.LeaveStatus;
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
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployee(Employee employee);
    Page<LeaveRequest> findByEmployee(Employee employee, Pageable pageable);
    List<LeaveRequest> findByStatus(LeaveStatus status);
    long countByStatus(LeaveStatus status);
    List<LeaveRequest> findByStatusIn(Collection<LeaveStatus> statuses);
    Page<LeaveRequest> findByStatusIn(Collection<LeaveStatus> statuses, Pageable pageable);

    /** Fetch leave requests with employee and employee.reportingTo so approval resolution works without lazy load. */
    @Query("SELECT lr FROM LeaveRequest lr JOIN FETCH lr.employee e LEFT JOIN FETCH e.reportingTo WHERE lr.status = :status")
    List<LeaveRequest> findByStatusWithEmployeeAndReportingTo(@Param("status") LeaveStatus status);

    long countByLeaveTypeId(Long leaveTypeId);

    List<LeaveRequest> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT lr FROM LeaveRequest lr JOIN FETCH lr.employee e LEFT JOIN FETCH e.department LEFT JOIN FETCH lr.leaveType WHERE lr.startDate BETWEEN :startDate AND :endDate")
    List<LeaveRequest> findByStartDateBetweenWithEmployeeAndLeaveType(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT lr FROM LeaveRequest lr JOIN FETCH lr.employee e LEFT JOIN FETCH e.department LEFT JOIN FETCH lr.leaveType WHERE e.department.id = :departmentId AND lr.startDate BETWEEN :startDate AND :endDate")
    List<LeaveRequest> findByDepartmentAndStartDateBetween(@Param("departmentId") Integer departmentId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find overlapping leave requests for an employee (PENDING or APPROVED only).
     * Overlap: newStart <= existingEnd AND newEnd >= existingStart
     */
    @Query("""
            SELECT lr FROM LeaveRequest lr
            WHERE lr.employee = :employee
            AND lr.status IN :statuses
            AND (:startDate <= lr.endDate AND :endDate >= lr.startDate)
            """)
    List<LeaveRequest> findOverlappingLeave(@Param("employee") Employee employee,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate,
                                            @Param("statuses") Collection<LeaveStatus> statuses);
}
