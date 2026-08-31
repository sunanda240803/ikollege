package com.iitm.hosteldine.repository.dashboard.student;

import com.iitm.hosteldine.model.dashboard.student.WorkflowMasterEntity;
import com.iitm.hosteldine.model.hostel.ShowEventMasterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WorkflowMasterRepository extends JpaRepository<WorkflowMasterEntity, Long> {
    Page<WorkflowMasterEntity> findAllByActiveFlag(String statusActive, Pageable pageable);

    @Query("select wrkflow from WorkflowMasterEntity wrkflow where wrkflow.activeFlag = :statusActive and (wrkflow.category ILIKE CONCAT('%', :search, '%')" +
            "or wrkflow.authorityType ILIKE CONCAT('%', :search, '%') or wrkflow.email ILIKE CONCAT('%', :search, '%') or wrkflow.validatorName ILIKE CONCAT('%', :search, '%'))")
    Page<WorkflowMasterEntity> getWorkflowMasterList(String statusActive, Pageable pageable, String search);

    @Query("select sse from WorkflowMasterEntity sse " +
            "where sse.category = :category " +
            "and sse.activeFlag = :statusActive " +
            "order by sse.approvalLevel")
    List<WorkflowMasterEntity> getWorkflowMasterList(String category, String statusActive);
    
    Optional<WorkflowMasterEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    List<WorkflowMasterEntity> findAllByCategoryAndActiveFlagOrderByAuthenticationTypeAscApprovalLevelAsc(String category, String activeFlag);

	@Query("SELECT sse FROM WorkflowMasterEntity sse " 
			+ "WHERE LOWER(sse.category) = LOWER(:category) AND sse.activeFlag = :activeFlag " 
			+ "ORDER BY sse.authenticationType ASC, sse.approvalLevel ASC")
	List<WorkflowMasterEntity> getAuthorityList(String category, String activeFlag);

}
