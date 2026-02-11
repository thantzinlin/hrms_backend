package com.hrms.repository;

import com.hrms.model.Attendance;
import com.hrms.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
