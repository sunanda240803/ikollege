package com.iitm.hosteldine.repository.OtherCandidate;

import com.iitm.hosteldine.model.OtherCandidate.StayExtensionRequestEntity;
import com.iitm.hosteldine.service.OtherCandidate.ExistingStayExtensionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StayExtensionRequestRepository extends JpaRepository<StayExtensionRequestEntity, Long> {

	Optional<StayExtensionRequestEntity> findByStayIdAndCandidateIdAndAppointmentIdAndActiveFlag(Long stayId,
			Long candidateId, Long requestId, String statusActive);

	Optional<StayExtensionRequestEntity> findByStayIdAndActiveFlag(Long stayId, String activeFlag);

    @Query(value = """
        select csr.stayId from StayExtensionRequestEntity csr where csr.candidateId = :applicationId
            and csr.stayFrom <= cast(:appointmentTo as date) and cast(:appointmentFrom as date) <= csr.stayTo and csr.appointmentId <> :requestId
                and csr.approvalStatus not in ('Deleted','Approved', 'Rejected','Cancelled')
    """)
    Optional<List<Long>> getStayExtensionRequests(Long requestId, Long applicationId, String appointmentFrom,
                                                      String appointmentTo);

	@Query(value = """
        select sere from StayExtensionRequestEntity sere join CandidateStayRequestWorkflowEntity csrw on 
            (sere.stayId = csrw.stayId) where csrw.id = :workflowId 
                AND (LOWER(csrw.authorityType) NOT LIKE LOWER(CONCAT('%', :ccw, '%')) AND LOWER(csrw.authorityType) NOT LIKE LOWER(CONCAT('%', :dean, '%')))
                and sere.approvalStatus = :approvalStatus
    """)
	Optional<StayExtensionRequestEntity> getCandidateStayRequestByStatus(Long workflowId, String approvalStatus,String ccw, String dean);

	@Query(value = """
        select csre.approvalStatus from CandidateStayRequestEntity  csre where csre.stayId = 
            (select csrw.stayId from CandidateStayRequestWorkflowEntity csrw where csrw.id = :workflowId) 
                and csre.approvalStatus not in ('Cancelled','Deleted') and csre.activeFlag = :activeFlag
    """)
	Optional<String> getStayApprovalStatus(Long workflowId, String activeFlag);

	@Query(value = """
	select new com.iitm.hosteldine.service.OtherCandidate.ExistingStayExtensionDetails(
		sere.stayFrom,sere.stayTo,sere.dining,sere.description,care.appointmentFrom,care.appointmentTo,care.stayFrom,care.stayTo,sere.approvalStatus,
		sere.statusNotes,care.grossPay,care.category,care.categoryOthers,care.dining,care.validatingAuthority,care.validatingAuthorityEmail,sere.messOption,
			care.messOption,sere.hostelId,sere.roomNo)
		from CandidateAppointmentRequestEntity care left join StayExtensionRequestEntity sere on (care.candidateId = sere.candidateId
		and sere.activeFlag = :activeFlag and care.approvalStatus in ('Approved','Alloted','Checkedin','Checkedout') and sere.stayId = :stayId)
			where care.candidateId = :candidateId and care.id = :requestId and care.activeFlag = :activeFlag
	""")
	Optional<ExistingStayExtensionDetails> getStayExtensionDetailsByRequestIdAndStayId(Long candidateId, Long requestId, Long stayId, String activeFlag);

	List<StayExtensionRequestEntity> findAllByCandidateIdAndAppointmentIdAndApprovalStatusAndActiveFlag(Long candidateId, Long appointmentId, String approvalStatus, String activeFlag);
}