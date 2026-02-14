package com.hrms.repository;

import com.hrms.model.Attendance;
import com.hrms.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByEmployeeAndDate(Employee employee, LocalDate date);
    List<Attendance> findByEmployeeAndDateBetween(Employee employee, LocalDate startDate, LocalDate endDate);
    Page<Attendance> findByEmployeeAndDateBetween(Employee employee, LocalDate startDate, LocalDate endDate, Pageable pageable);
    List<Attendance> findByDate(LocalDate date);

    @Query("SELECT a FROM Attendance a JOIN FETCH a.employee e LEFT JOIN FETCH e.department WHERE a.date BETWEEN :startDate AND :endDate ORDER BY a.date DESC, e.name ASC")
    List<Attendance> findByDateBetweenWithEmployee(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT a FROM Attendance a JOIN FETCH a.employee e LEFT JOIN FETCH e.department WHERE e.department.id = :departmentId AND a.date BETWEEN :startDate AND :endDate ORDER BY a.date DESC, e.name ASC")
    List<Attendance> findByDepartmentAndDateBetween(@Param("departmentId") Integer departmentId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
