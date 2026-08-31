package com.iitm.hosteldine.repository.OtherCandidate;

import com.iitm.hosteldine.model.OtherCandidate.CandidateStayRequestWorkflowEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateStayRequestWorkflowRepository extends JpaRepository<CandidateStayRequestWorkflowEntity, Long> {

    Optional<CandidateStayRequestWorkflowEntity> findByStayIdAndCandidateIdAndActiveFlag(Long stayId,
                                                                                         Long candidateId, String activeFlag);

    @Transactional
    @Modifying
    @Query(value = """
        update CandidateStayRequestWorkflowEntity csrw
            set csrw.approvalStatus = 'Cancelled', csrw.modifiedBy = CAST(:applicationId AS string), csrw.modifiedAt = CURRENT_TIMESTAMP
                where csrw.appointmentId in (select csr.appointmentId from CandidateStayRequestEntity csr where csr.stayId = :stayId)
    """)
    void cancelStayRequestWorkflow(Long stayId, Long applicationId);

    @Query("""
        select csrw from CandidateStayRequestWorkflowEntity csrw where (csrw.approvalStatus = 'Pending' or csrw.approvalStatus = 'Rejected' or 
            csrw.approvalStatus = 'Default') and csrw.id = :workflowId and csrw.activeFlag = :activeFlag
    """)
    Optional<CandidateStayRequestWorkflowEntity> getWorkflowByStatus(Long workflowId, String activeFlag);

    Optional<CandidateStayRequestWorkflowEntity> findByIdAndActiveFlag(Long workflowId, String activeFlag);

    @Query(value = """
        select * from  schooldev.process_workflow_stay_extension(:workflowId,:status,:rejectReason,:approvalNotes,:modifiedBy)
    """,nativeQuery = true)
    List<Object[]> processStayWorkflow(Long workflowId, String status, String rejectReason, String approvalNotes, String modifiedBy);
}
