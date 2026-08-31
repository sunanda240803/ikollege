package com.iitm.hosteldine.repository.mailQueue;

import com.iitm.hosteldine.entity.mailQueue.MailTemplateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MailTemplateRepository extends JpaRepository<MailTemplateEntity, String> {
    public Optional<MailTemplateEntity> findByMailType(String mailType);

    Page<MailTemplateEntity> findAllByActiveFlagOrderByMailType(String activeFlag, Pageable pageable);

    Optional<MailTemplateEntity> findByActiveFlagAndMailType(String activeFlag,String mailType);

    Optional<MailTemplateEntity> findByCategoryIgnoreCaseAndApprovalLevelAndAuthorityTypeAndActiveFlag(String category, Long approvalLevel,
                                                                                             String authorityType,String activeFlag);
}
