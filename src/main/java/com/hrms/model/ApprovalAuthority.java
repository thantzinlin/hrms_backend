package com.hrms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "approval_authorities")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalAuthority extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private Employee employee;

    @Column(name = "can_approve_leave", nullable = false)
    private Boolean canApproveLeave = false;

    @Column(name = "can_approve_overtime", nullable = false)
    private Boolean canApproveOvertime = false;

    @Column(name = "can_approve_claim", nullable = false)
    private Boolean canApproveClaim = false;

    @Column(name = "is_hr", nullable = false)
    private Boolean isHr = false;
}
