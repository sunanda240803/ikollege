package com.iitm.hosteldine.repository.OtherCandidate;

import com.iitm.hosteldine.model.OtherCandidate.CandidateStayDateIdEntity;
import com.iitm.hosteldine.model.OtherCandidate.CandidateStayDateViewEntity;
import com.iitm.hosteldine.service.OtherCandidate.PreviousStayExtensionDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CandidateStayDateViewRepository extends JpaRepository<CandidateStayDateViewEntity,
        CandidateStayDateIdEntity> {

    Page<CandidateStayDateViewEntity> findAllById_CandidateId(Long candidateId, Pageable pageable);

    /*@Query("""
                select csdve.appStatus, csdve.id.stayId,csdve.stayStatus
                    from CandidateStayDateViewEntity csdve
                        where candidate_id = :applicationId and stay_from <= :appointmentTo
                            and :appointmentFrom <= stay_to and request_id <> :requestId
                                and case when stay_id <> 0 then stay_id <> :stayId
                                    else stay_id = 0 end
                                        and case when stay_id <> 0 then stay_status else app_status 
                                            end not in ('Deleted', 'Cancelled', 'Rejected') order by "case" desc
            """)
    Optional<List<Object[]>> getPreviousStayPeriodDetails(Long applicationId, Long requestId, Long stayId,
                                                          LocalDate appointmentFrom, LocalDate appointmentTo);*/


    @Query("""
            select csdve.appStatus, csdve.id.stayId, csdve.stayStatus
            from CandidateStayDateViewEntity csdve
            where csdve.id.candidateId = :applicationId 
              and csdve.stayFrom <= :appointmentTo 
              and :appointmentFrom <= csdve.stayTo 
              and csdve.id.requestId <> :requestId
              and (
                  (csdve.id.stayId <> 0 and csdve.id.stayId <> :stayId) 
                  or (csdve.id.stayId = 0)
              )
              and (
                  (csdve.id.stayId <> 0 and csdve.stayStatus not in ('Deleted', 'Cancelled', 'Rejected'))
                  or (csdve.id.stayId = 0 and csdve.appStatus not in ('Deleted', 'Cancelled', 'Rejected'))
              )
            order by csdve.caseStatus desc
            """)
    List<Object[]> findConflictingAppointments(Long applicationId, Long requestId, Long stayId,
                                               LocalDate appointmentFrom, LocalDate appointmentTo);

    @Query("""
                select new com.iitm.hosteldine.service.OtherCandidate.PreviousStayExtensionDetails(
            			        csde.id.candidateId,
            			        csde.id.requestId,
            			        csde.id.stayId,
            			        csde.appointmentFrom,
            			        csde.appointmentTo,
            			        csde.stayFrom,
            			        csde.stayTo,
            			        csde.appStatus,
            			        csrw.approvalNotes,
            			        csre.approvalDate,
            			        csre.dining,
            			        csrw.validatorName,
            			        csrw.validatorEmail,
            			        csrw.authorityType,
            			        csrw.approvalStatus,
            			        csre.messOption,
            			        hme.hostelName,
            			        csre.roomNo)
                from CandidateStayDateViewEntity csde 
                join CandidateStayRequestEntity csre on (
                    csde.id.candidateId = csre.candidateId
                    and csde.id.requestId = csre.appointmentId
                    and csde.id.stayId = csre.stayId)
                join CandidateStayRequestWorkflowEntity csrw on (
                    csrw.appointmentId = csre.appointmentId
                    and csrw.approvalStatus = 'Approved'
                    and csrw.activeFlag = 'Y'
                    and csrw.stayId = csre.stayId)
                left join HostelMasterEntity hme on (hme.id = csre.hostelId)
                where csde.id.candidateId = :candidateId 
                and csde.id.requestId = :requestId 
                and csde.id.stayId != 0 
                and csde.id.stayId < :stayId
                and csde.stayStatus in (:status)
                and csde.appStatus in (:status)
                and (LOWER(csrw.authorityType) LIKE LOWER(CONCAT('%', :ccw, '%')) OR LOWER(csrw.authorityType) LIKE LOWER(CONCAT('%', :dean, '%')))
                order by csde.id.stayId
            """)
    List<PreviousStayExtensionDetails> getPreviousStayExtensionDetails(Long requestId, Long candidateId, Long stayId
                                                                        ,List<String> status,String ccw, String dean);
}
