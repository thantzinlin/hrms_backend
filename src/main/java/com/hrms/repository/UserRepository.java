package com.hrms.repository;

import com.hrms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<User> findByEmailAndIsDeletedFalse(String email); // Use 'email' if that is the
    // correct field for login

    Optional<User> findByUsernameAndIsDeletedFalse(String name);

    Optional<User> findByUserIdAndIsDeletedFalse(String userId);

    Optional<User> findByResettoken(String token);

    @Query("SELECT MAX(u.userId) FROM User u")
    String findMaxUserId();

    @Query("""
                SELECT u FROM User u
                LEFT JOIN FETCH u.roles
                WHERE u.username = :username
            """)
    Optional<User> findByUsernameWithRoles(@Param("username") String username);

    @Query("""
            SELECT u FROM User u
            WHERE u.isDeleted = false AND (
                :search IS NULL OR :search = '' OR
                LOWER(u.userId) LIKE LOWER(CONCAT('%', :search, '%')) OR
                LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR
                LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
            )
            """)
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);

    Page<User> findByIsDeletedFalse(Pageable pageable);
}
