package com.hrms.service;

import com.hrms.model.*;
import com.hrms.repository.ApprovalAuthorityRepository;
import com.hrms.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Resolves approvers dynamically from hierarchy and approval_authorities.
 * No hardcoded roles; HR and supervisor approval are config-driven.
 */
@Service
public class ApprovalResolutionService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ApprovalAuthorityRepository approvalAuthorityRepository;

    /**
     * Traverse reporting hierarchy upward and return the nearest supervisor
     * who has authority to approve the given request type.
     */
    public Optional<Employee> resolveSupervisorApprover(Employee requester, RequestType requestType) {
        if (requester == null || requester.getReportingTo() == null) {
            return Optional.empty();
        }
        Employee current = requester.getReportingTo();
        while (current != null) {
            Optional<ApprovalAuthority> auth = approvalAuthorityRepository.findByEmployee(current);
            if (auth.isPresent()) {
                boolean canApprove = requestType == RequestType.LEAVE
                        ? auth.get().getCanApproveLeave()
                        : auth.get().getCanApproveOvertime();
                if (Boolean.TRUE.equals(canApprove)) {
                    return Optional.of(current);
                }
            }
            current = current.getReportingTo();
        }
        return Optional.empty();
    }

    /**
     * Return an HR approver from approval_authorities (is_hr = true).
     * Any HR can perform final approval; no hardcoded user/email.
     */
    public Optional<Employee> resolveHrApprover() {
        return approvalAuthorityRepository.findByIsHrTrue().stream()
                .findFirst()
                .map(ApprovalAuthority::getEmployee);
    }

    /**
     * Check if the given employee can approve at supervisor level for this request
     * type.
     */
    public boolean canApproveAsSupervisor(Employee employee, RequestType requestType) {
        if (employee == null)
            return false;
        return approvalAuthorityRepository.findByEmployee(employee)
                .map(a -> requestType == RequestType.LEAVE ? a.getCanApproveLeave() : a.getCanApproveOvertime())
                .orElse(false);
    }

    /**
     * Check if the given employee has HR approval authority.
     */
    public boolean isHrApprover(Employee employee) {
        if (employee == null)
            return false;
        return approvalAuthorityRepository.findByEmployee(employee)
                .map(ApprovalAuthority::getIsHr)
                .orElse(false);
    }
}
