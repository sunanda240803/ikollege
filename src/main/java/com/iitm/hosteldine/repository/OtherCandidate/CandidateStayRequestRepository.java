package com.iitm.hosteldine.repository.OtherCandidate;

import com.iitm.hosteldine.model.OtherCandidate.CandidateStayRequestEntity;
import com.iitm.hosteldine.service.OtherCandidate.CurrentStayExtensionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateStayRequestRepository extends JpaRepository<CandidateStayRequestEntity,Long> {

    Optional<CandidateStayRequestEntity> findByStayIdAndActiveFlag(Long stayId, String activeFlag);

    @Query(value = """
        select csr.stayId from CandidateStayRequestEntity csr where csr.candidateId = :applicationId
            and csr.stayFrom <= :appointmentTo and :appointmentFrom <= csr.stayTo and csr.appointmentId <> :requestId
                and csr.approvalStatus not in ('Deleted','Approved', 'Rejected','Cancelled')
    """)
    Optional<List<Long>> getStayExtensionRequests(Long requestId, Long applicationId, LocalDate appointmentFrom,
                                                      LocalDate appointmentTo);

    @Query("""
    select new com.iitm.hosteldine.service.OtherCandidate.CurrentStayExtensionDetails(
        csre.stayFrom,
        csre.stayTo,
        csre.dining,
        csre.description,
        csre.candidateId,
        csre.appointmentId,
        csrw.modifiedAt,
        csrw.id,
        csrw.approvalNotes,
        csrw.rejectionDescription,
        csre.messOption,
        hm.hostelName,
        csre.roomNo)
    from CandidateStayRequestEntity csre 
    left join CandidateStayRequestWorkflowEntity csrw 
        on (csrw.stayId = csre.appointmentId and upper(csrw.authorityType) like concat('%', upper(:authorityType), '%') and csrw.activeFlag = 'Y') 
    left join HostelMasterEntity hm 
        on (hm.id = csre.hostelId)
    where csre.stayId = :stayId and csre.activeFlag = :activeFlag
""")
    Optional<CurrentStayExtensionDetails> getCurrentStayExtensionDetails(Long stayId, String activeFlag, String authorityType);

}