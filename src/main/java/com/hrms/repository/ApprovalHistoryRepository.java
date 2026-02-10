package com.hrms.repository;

import com.hrms.model.ApprovalLevel;
import com.hrms.model.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hrms.model.ApprovalHistory;

import java.util.List;

@Repository
public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {
    List<ApprovalHistory> findByRequestTypeAndRequestIdOrderByActionDateAsc(RequestType requestType, Long requestId);
}
