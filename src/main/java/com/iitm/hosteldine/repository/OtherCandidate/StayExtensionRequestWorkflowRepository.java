package com.iitm.hosteldine.repository.OtherCandidate;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iitm.hosteldine.model.OtherCandidate.CandidateStayRequestWorkflowEntity;
import com.iitm.hosteldine.model.OtherCandidate.StayExtensionRequestWorkflowEntity;

import jakarta.transaction.Transactional;

@Repository
public interface StayExtensionRequestWorkflowRepository extends JpaRepository<StayExtensionRequestWorkflowEntity, Long> {

	
	@Query("SELECT s FROM StayExtensionRequestWorkflowEntity s WHERE s.stayId = :stayId AND s.activeFlag = :statusActive " +
		       "AND s.approvalStatus <> 'Default' ORDER BY s.approvalLevel")
	List<StayExtensionRequestWorkflowEntity> getStauWorkflowRequestList(Long stayId, String statusActive,
			String status);

	List<StayExtensionRequestWorkflowEntity> findByCandidateIdAndAppointmentIdAndStayIdAndActiveFlag(
			Long candidateId, Long requestId, Long stayId, String statusActive);

    @Modifying
    @Transactional
	@Query("UPDATE StayExtensionRequestWorkflowEntity SET activeFlag = 'N', "
			+ "modifiedBy = :applicationId, modifiedAt = CURRENT_TIMESTAMP  WHERE candidateId = :candidateId AND appointmentId = :requestId "
			+ "AND stayId = :stayId AND activeFlag = 'Y' ")
	int updateWorkflowRequestToInactive(String applicationId, Long candidateId, Long requestId, Long stayId, String no);

	List<StayExtensionRequestWorkflowEntity> findAllByCandidateIdAndAppointmentIdAndStayIdAndActiveFlag(
			Long candidateId, Long requestId, Long stayId, String statusActive);
	
	Optional<StayExtensionRequestWorkflowEntity> findByStayIdAndCandidateIdAndActiveFlag(Long stayId,
            Long candidateId, String activeFlag);

	@Transactional
	@Modifying
	@Query(value = """
	update StayExtensionRequestWorkflowEntity csrw
	set csrw.approvalStatus = 'Cancelled', csrw.modifiedBy = CAST(:applicationId AS string), csrw.modifiedAt = CURRENT_TIMESTAMP
	where csrw.appointmentId in (select csr.appointmentId from StayExtensionRequestWorkflowEntity csr where csr.stayId = :stayId)
	""")
	void cancelStayRequestWorkflow(Long stayId, Long applicationId);

	List<StayExtensionRequestWorkflowEntity> findAllByCandidateIdAndAppointmentIdAndStayIdAndApprovalStatusInAndActiveFlag(Long candidateId, Long appointmentId, Long stayId, List<String> approvalStatus,
																				String activeFlag);
}