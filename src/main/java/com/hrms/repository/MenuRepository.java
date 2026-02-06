package com.hrms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hrms.model.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findByIsDeletedFalseOrderBySequenceAsc();

    @Query("""
          SELECT DISTINCT m
          FROM Role r
          JOIN r.menus m
          WHERE r.roleId IN :roleIds
            AND m.isDeleted = false
          ORDER BY m.sequence
      """)
  List<Menu> findMenusByRoles(
      List<Long> roleIds);

}
