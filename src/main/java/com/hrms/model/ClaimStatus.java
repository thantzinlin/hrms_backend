package com.hrms.model;

/** Status of an expense claim in the approval workflow. */
public enum ClaimStatus {
    /** Draft - not yet submitted, can be edited. */
    DRAFT,

    /** Submitted, awaiting supervisor approval. */
    PENDING_SUPERVISOR,

    /** Supervisor approved, awaiting HR approval. */
    PENDING_HR,

    /** Fully approved, ready for reimbursement. */
    APPROVED,

    /** Rejected at any stage. */
    REJECTED,

    /** Reimbursement completed. */
    REIMBURSED
}
