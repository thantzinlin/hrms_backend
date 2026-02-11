package com.hrms.repository;

import com.hrms.model.Claim;
import com.hrms.model.ClaimStatus;
import com.hrms.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    Page<Claim> findByEmployee(Employee employee, Pageable pageable);

    List<Claim> findByEmployee(Employee employee);

    Page<Claim> findByEmployee_EmployeeId(String employeeId, Pageable pageable);

    Page<Claim> findByStatusIn(List<ClaimStatus> statuses, Pageable pageable);

    List<Claim> findByStatusIn(List<ClaimStatus> statuses);

    Optional<Claim> findByClaimNumber(String claimNumber);

    @Query("SELECT c FROM Claim c LEFT JOIN FETCH c.employee LEFT JOIN FETCH c.claimType WHERE c.status IN :statuses")
    List<Claim> findByStatusInWithEmployeeAndClaimType(@Param("statuses") List<ClaimStatus> statuses);

    @Query("SELECT c FROM Claim c JOIN FETCH c.employee e LEFT JOIN FETCH e.reportingTo WHERE c.status = :status")
    List<Claim> findByStatusWithEmployeeAndReportingTo(@Param("status") ClaimStatus status);

    List<Claim> findByStatus(ClaimStatus status);

    @Query("SELECT COUNT(c) FROM Claim c WHERE c.status IN :statuses")
    long countByStatusIn(@Param("statuses") List<ClaimStatus> statuses);

    @Query("SELECT MAX(c.id) FROM Claim c")
    Optional<Long> findMaxId();

    long countByClaimTypeId(Long claimTypeId);
}
