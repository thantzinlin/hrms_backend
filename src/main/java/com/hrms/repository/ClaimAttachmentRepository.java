package com.hrms.repository;

import com.hrms.model.Claim;
import com.hrms.model.ClaimAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimAttachmentRepository extends JpaRepository<ClaimAttachment, Long> {

    List<ClaimAttachment> findByClaim(Claim claim);
}
