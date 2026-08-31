package com.iitm.hosteldine.repository.mess;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.mess.MessRebateWorkflowEntity;

import java.util.List;
import java.util.Optional;

public interface MessRebateWorkflowRepository extends JpaRepository<MessRebateWorkflowEntity, Long> {
    Optional<MessRebateWorkflowEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    List<MessRebateWorkflowEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    @Query(value = """
                select count(x) from MessRebateWorkflowEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);

	List<MessRebateWorkflowEntity> findAllByRequestIdAndActiveFlagAndAuthenticationTypeOrderByModifiedAtDesc(
			Long requestId, String activeFlag, String authenticationType);
	
    Optional<MessRebateWorkflowEntity> findByRequestIdAndStudentIdAndAuthorityTypeAndActiveFlag(Long requestId, String studentId, String authorityType, String activeFlag);
    
    List<MessRebateWorkflowEntity> findByRequestIdAndStudentIdAndActiveFlag(Long requestId, String studentId, String activeFlag);
    
}