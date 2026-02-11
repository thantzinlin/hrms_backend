package com.hrms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "reportingTo")
public class Employee extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column(unique = true, nullable = false)
    private String employeeId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;

    @Column(nullable = false)
    private LocalDateTime joinDate;

    @Column(nullable = false)
    private String status;

    @Column(name = "father_name", length = 255)
    private String fatherName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 100)
    private String nationality;

    @Column(length = 100)
    private String race;

    @Column(length = 50)
    private String gender;

    @Column(name = "marital_status", length = 50)
    private String maritalStatus;

    @Column(length = 100)
    private String nrc;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position jobPosition;

    /** Reporting manager (self-referencing). Used for hierarchy-based approval. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporting_to")
    private Employee reportingTo;
}
