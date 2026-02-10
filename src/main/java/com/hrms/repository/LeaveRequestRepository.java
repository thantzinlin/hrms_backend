package com.hrms.repository;

import com.hrms.model.Employee;
import com.hrms.model.LeaveRequest;
import com.hrms.model.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployee(Employee employee);
    List<LeaveRequest> findByStatus(LeaveStatus status);
    List<LeaveRequest> findByStatusIn(Collection<LeaveStatus> statuses);

    /** Fetch leave requests with employee and employee.reportingTo so approval resolution works without lazy load. */
    @Query("SELECT lr FROM LeaveRequest lr JOIN FETCH lr.employee e LEFT JOIN FETCH e.reportingTo WHERE lr.status = :status")
    List<LeaveRequest> findByStatusWithEmployeeAndReportingTo(@Param("status") LeaveStatus status);
}
