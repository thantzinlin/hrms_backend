package com.hrms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "leave_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveType extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(name = "max_days")
    private Integer maxDays;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
