package com.iitm.hosteldine.repository.mess;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.mess.MessRebateEntity;

public interface MessRebateRepository extends JpaRepository<MessRebateEntity, Long> {
	
	MessRebateEntity findTopByStudentIdAndActiveFlagOrderByModifiedAtDesc(String studentId, String activeFlag);

	MessRebateEntity findByIdAndActiveFlag(Long id, String statusActive);

    Page<MessRebateEntity> findAllByStudentIdAndActiveFlag(String studentId, String statusActive, Pageable pageable);

	@Query("""
			SELECT COUNT(*) FROM MessRebateEntity WHERE studentId = :studentId 
				AND approvalStatus <> :approvalStatus
				AND :rebateFrom <= rebateTo
               	AND :rebateTo >= rebateFrom
					""")
	Integer checkFromDateAndToDateExists(String studentId, LocalDate rebateFrom, LocalDate rebateTo, String approvalStatus);

	@Query(value = """
		    SELECT 
		        created_at,student_id,leave_from,leave_to,rebate_from,rebate_to,no_of_days,rebate_reason,studstatus,id,guide_auth_name,
		        guide_approval_status,guide_approval_date,ccw_auth_name,ccw_approval_status,ccw_approval_date
				from schooldev.search_students_rebate(NULL, 'null', 'null', 'null', 'null', 'null', 'null', NULL, NULL, NULL, NULL, 'CCW Dean', NULL, NULL,'Caterer','sakthi.ms') """, nativeQuery = true)
	Page<Object[]> getMessApprovalList(Pageable pageable);

	@Query(value = """
		    SELECT 
		        created_at,student_id,leave_from,leave_to,rebate_from,rebate_to,no_of_days,rebate_reason,studstatus,id,guide_auth_name,
		        guide_approval_status,guide_approval_date,ccw_auth_name,ccw_approval_status,ccw_approval_date
				from schooldev.search_students_rebate(CAST(:validationStatus AS VARCHAR), CAST(:approvalFromDate AS VARCHAR), CAST(:approvalToDate AS VARCHAR), CAST(:submittedFromDate AS VARCHAR), CAST(:submittedToDate AS VARCHAR), 
				CAST(:rebateFromDate AS VARCHAR), CAST(:rebateToDate AS VARCHAR), CAST(:studentName AS VARCHAR), CAST(:studentId AS VARCHAR), CAST(:hodName AS VARCHAR), 
				CAST(:hodEmail AS VARCHAR), CAST(:authorityType AS VARCHAR), CAST(:siNoFrom AS VARCHAR), CAST(:siNoTo AS VARCHAR),CAST(:role AS VARCHAR),CAST(:loginId AS VARCHAR)) """, nativeQuery = true)
	Object[] getMessApprovalListByFilter(String validationStatus, LocalDate approvalFromDate,
			LocalDate approvalToDate, LocalDate submittedFromDate, LocalDate submittedToDate, LocalDate rebateFromDate,
			LocalDate rebateToDate, String studentName, String studentId, String hodName, String hodEmail, String authorityType,
			String siNoFrom, String siNoTo, String role, String loginId);
	
	@Query(value = """
			select sdv.student_id,sdv.student_name,sdv.dob,sdv.gender,sdv.student_address,sdv.city,sdv.pin_code,sdv.state,sdv.student_mobile,
			sdv.student_iitm_smail,
			mr.leave_from,mr.leave_to,mr.rebate_from,mr.rebate_to,mr.rebate_reason,mr.no_of_days,mr.guide_name,
			mr.guide_email,mr.doc_status,mr.approval_status,mr.id,mr.cancel_status,mr.validator_name, mr.validator_email,mr.vacate_date,mr.hometown,
			mr.self_dec_date,self_dec_signature,sdv.bio_data_id,mr.file_upload
			from schooldev."IIT_A_MESS_REBATE" mr
			left join schooldev."ALL_STUDENTS_DETAILS_VIEW" sdv on (mr.student_id = sdv.student_id)
			where UPPER(TRIM(mr.student_id)) = UPPER(TRIM(:studentId))
			and mr.id = :id
						""", nativeQuery = true)
	Object[] getMessRebateDetails(String studentId, Long id);
	
	@Query(value = """
			select mrw.id,mrw.authority_type,mrw.approval_status,mrw.approval_notes,mrw.rejection_description,mrw.approval_level
			from schooldev."IIT_A_MESS_REBATE" mr 
			join schooldev."IIT_A_MESS_REBATE_WORKFLOW" mrw on (mrw.student_id = mr.student_id and mrw.request_id = mr.id)
			where UPPER(TRIM(mr.student_id)) = UPPER(TRIM(:studentId))
			and mr.id = :id
			and UPPER(TRIM(mrw.authority_type)) = UPPER(TRIM(:role))
						""", nativeQuery = true)
	Object[] getMessRebateWorkFlowDetailsByAuthority(String studentId, Long id, String role);
	
	
	
	@Query(value = """
			select authority_type, approval_level,guide_name,guide_email,authentication_type,approval_status,
			modified_at,approval_notes,rejection_description from schooldev."IIT_A_MESS_REBATE_WORKFLOW"  
			where UPPER(TRIM(student_id)) = UPPER(TRIM(:studentId))
			and request_id = :id
			and active_flag = 'Y'
						""", nativeQuery = true)
	List<Object[]> getMessRebateWorkFlowDetails(String studentId, Long id);

    
}