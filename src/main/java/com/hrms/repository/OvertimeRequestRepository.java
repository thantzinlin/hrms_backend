package com.hrms.repository;

import com.hrms.model.Employee;
import com.hrms.model.OvertimeRequest;
import com.hrms.model.OvertimeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OvertimeRequestRepository extends JpaRepository<OvertimeRequest, Long> {
    List<OvertimeRequest> findByEmployee(Employee employee);
    List<OvertimeRequest> findByStatus(OvertimeStatus status);
}
