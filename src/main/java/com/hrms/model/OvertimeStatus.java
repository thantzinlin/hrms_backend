package com.hrms.model;

public enum OvertimeStatus {
    /** Awaiting supervisor/manager approval (hierarchy-based). */
    PENDING_SUPERVISOR,
    /** Supervisor approved; awaiting HR approval. */
    PENDING_HR,
    /** Fully approved (supervisor + HR). */
    APPROVED,
    /** Rejected at any stage. */
    REJECTED
}
