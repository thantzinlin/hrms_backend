package com.hrms.repository;

import com.hrms.model.ClaimType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimTypeRepository extends JpaRepository<ClaimType, Long> {

    Optional<ClaimType> findByCode(String code);

    List<ClaimType> findByIsActiveTrueOrderByNameAsc();

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);
}
