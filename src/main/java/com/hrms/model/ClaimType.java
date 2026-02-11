package com.hrms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "claim_types")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ClaimType extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    /** Maximum amount allowed per claim (null = no limit). */
    @Column(name = "max_amount_per_claim", precision = 19, scale = 4)
    private BigDecimal maxAmountPerClaim;

    /** Whether this claim type requires receipt/invoice. */
    @Column(name = "requires_receipt", nullable = false)
    private Boolean requiresReceipt = false;

    /** Default currency for this claim type (e.g. USD, BDT). */
    @Column(length = 10)
    private String currency = "USD";

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
