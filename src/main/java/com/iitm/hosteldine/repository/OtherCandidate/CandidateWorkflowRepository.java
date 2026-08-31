package com.iitm.hosteldine.repository.OtherCandidate;

import com.iitm.hosteldine.model.OtherCandidate.CandidateWorkflowEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateWorkflowRepository extends JpaRepository<CandidateWorkflowEntity, Long> {
    Optional<List<CandidateWorkflowEntity>> findAllByCandidateIdAndApplicationIdAndActiveFlagAndStatusNot(Long candidateId,
                                                                                                 Long applicationId,
                                                                                                 String activeFlag,
                                                                                                 String status);

    Optional<CandidateWorkflowEntity> findByCandidateIdAndApplicationId(Long candidateId, Long applicationId);

    Optional<List<CandidateWorkflowEntity>> findByApplicationIdAndCategoryAndStatusAndActiveFlag(Long applicationId,
                                                                                                 String category, String status, String activeFlag);

    Optional<List<CandidateWorkflowEntity>> findAllByCandidateIdAndApplicationIdAndActiveFlag(Long candidateId,
                                                                                              Long applicationId,String activeFlag);

    Optional<List<CandidateWorkflowEntity>> findAllByIdAndActiveFlag(Long id,String activeFlag);


    @Transactional
    @Modifying
    @Query(value = """
            UPDATE CandidateWorkflowEntity cwf
            SET cwf.status = 'Cancelled'
            WHERE cwf.applicationId IN (
                SELECT care.id
                FROM CandidateAppointmentRequestEntity care
                WHERE care.candidateId = :candidateId
                  AND care.stayFrom <= cast(:appointmentTo as date)
                  AND cast(:appointmentFrom as date) <= care.stayTo
                  AND care.id <> :requestId
                  AND care.approvalStatus IN ('Cancelled')
            )
            """)
    void cancelWorkflowDetails(Long requestId, Long candidateId, String appointmentFrom, String appointmentTo);

    @Query(value = """
    select cwe from CandidateWorkflowEntity cwe where cwe.candidateId = :candidateId and cwe.applicationId = :requestId
        and cwe.approvalLevel = (
            select MIN(icwc.approvalLevel) from CandidateWorkflowEntity icwc where icwc.candidateId = :candidateId and
                icwc.applicationId = :requestId and icwc.approvalLevel > :approvalLevel
            )
    """)
    List<CandidateWorkflowEntity> getNextLevelApprovalList(Long candidateId,Long requestId,Integer approvalLevel);



    @Transactional
    @Modifying
    @Query(value = """
            UPDATE CandidateWorkflowEntity cwf
            SET cwf.status = 'Deleted'
            WHERE cwf.candidateId = :candidateId and cwf.applicationId = :requestId and cwf.activeFlag = :activeFlag
             AND (LOWER(cwf.authorityType) LIKE LOWER(CONCAT('%', :ccw, '%')) or LOWER(cwf.authorityType) LIKE LOWER(CONCAT('%', :dean, '%')))
            """)
    int deleteWorkflowDetails(Long requestId, Long candidateId, String activeFlag,String ccw, String dean);

    @Query("""
        select cwe from CandidateWorkflowEntity cwe where (cwe.status = 'Pending' or cwe.status = 'Rejected' or 
            cwe.status = 'Default') and cwe.id = :workflowId and cwe.activeFlag = :activeFlag
    """)
    Optional<CandidateWorkflowEntity> getByStatus(Long workflowId, String activeFlag);

    Optional<CandidateWorkflowEntity> findByIdAndActiveFlag(Long workflowId, String activeFlag);


    @Query(value = """
        select * from schooldev.process_workflow(:workflowId,:modifiedAt,:status,:rejectReason,:approvalNotes,:occupancy,:accomPriority, :modifiedBy)
    """,nativeQuery = true)
    List<Object[]> processWorkflow(Long workflowId, LocalDateTime modifiedAt, String status, String rejectReason, String approvalNotes,
                                   String occupancy, Long accomPriority, String modifiedBy);


//    Optional<CandidateWorkflowEntity> findByAuthorityTypeContainingIgnoreCaseAndApplicationIdAndActiveFlag(String authorityType,Long applicationId,String activeFlag);

    @Query("""
    SELECT c FROM CandidateWorkflowEntity c
    WHERE c.applicationId = :applicationId AND c.activeFlag = :activeFlag
      AND (LOWER(c.authorityType) LIKE LOWER(CONCAT('%', :ccw, '%')) OR LOWER(c.authorityType) LIKE LOWER(CONCAT('%', :dean, '%')))
""")
    Optional<CandidateWorkflowEntity> findByAuthorityTypeCCWOrDean(
            @Param("ccw") String ccw,
            @Param("dean") String dean,
            @Param("applicationId") Long applicationId,
            @Param("activeFlag") String activeFlag
    );

    List<CandidateWorkflowEntity> findAllByCandidateIdAndApplicationIdAndActiveFlagOrderByApprovalLevelAsc(Long candidateId, Long applicationId, String activeFlag);
    
}
