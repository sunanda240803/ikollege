package com.iitm.hosteldine.repository;

import com.iitm.hosteldine.model.UserAuditTrailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuditTrailRepository extends JpaRepository<UserAuditTrailEntity, Long> {
}