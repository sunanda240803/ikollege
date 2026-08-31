package com.iitm.hosteldine.repository.mess;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.mess.StudentMessChangeWorkflowEntity;

import java.util.List;
import java.util.Optional;

public interface StudentMessChangeWorkflowRepository extends JpaRepository<StudentMessChangeWorkflowEntity, Long> {
    Optional<StudentMessChangeWorkflowEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    List<StudentMessChangeWorkflowEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    @Query(value = """
                select count(x) from StudentMessChangeWorkflowEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);

	@Query(value = """
			SELECT smcw.id, smcw.studentId, sbd.studentName, smcw.originallyMessFromDate, smcw.originallyMessToDate,
			smcw.requestedMessFromDate as effective_from_date, rmm.messHead as requested_mess_name,
			omm.messHead as originally_mess_name, smcw.description, smcw.approvalStatus
			FROM StudentMessChangeWorkflowEntity smcw
			LEFT JOIN StudentBioDataFormDetailEntity sbd ON (sbd.studentId = smcw.studentId AND sbd.activeFlag = :activeFlag)
			LEFT JOIN MessMasterEntity rmm ON (rmm.id = smcw.requestedMessId)
			LEFT JOIN MessMasterEntity omm ON (omm.id = smcw.originallyMessId)
			WHERE smcw.activeFlag = :activeFlag AND smcw.mmcId = :mmcId
			ORDER BY smcw.studentId
			            """)
	Page<Object[]> getMessChangeRequestsList(String activeFlag, Long mmcId, Pageable pageable);

	@Query(value = """
			SELECT smcw.id, smcw.studentId, sbd.studentName, smcw.originallyMessFromDate, smcw.originallyMessToDate,
			smcw.requestedMessFromDate as effective_from_date, rmm.messHead as requested_mess_name,
			omm.messHead as originally_mess_name, smcw.description, smcw.approvalStatus
			FROM StudentMessChangeWorkflowEntity smcw
			LEFT JOIN StudentBioDataFormDetailEntity sbd ON (sbd.studentId = smcw.studentId AND sbd.activeFlag = :activeFlag)
			LEFT JOIN MessMasterEntity rmm ON (rmm.id = smcw.requestedMessId)
			LEFT JOIN MessMasterEntity omm ON (omm.id = smcw.originallyMessId)
			WHERE smcw.activeFlag = :activeFlag AND smcw.mmcId = :mmcId 
			AND (smcw.studentId ILIKE CONCAT('%', :search, '%') OR 
			sbd.studentName ILIKE CONCAT('%', :search, '%') OR 
			omm.messHead ILIKE CONCAT('%', :search, '%') OR 
			rmm.messHead ILIKE CONCAT('%', :search, '%') OR
			smcw.description ILIKE CONCAT('%', :search, '%'))
			ORDER BY smcw.studentId
			            """)
	Page<Object[]> getMessChangeRequestsListByFormSearch(String activeFlag, Long mmcId, Pageable pageable, String search);
}