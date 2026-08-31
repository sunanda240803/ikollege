package com.iitm.hosteldine.repository.OtherCandidate;

import com.iitm.hosteldine.model.OtherCandidate.CandidateAppointmentRequestEntity;
import com.iitm.hosteldine.service.OtherCandidate.RequestDetails;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CandidateAppointmentRequestRepository extends JpaRepository<CandidateAppointmentRequestEntity, Long> {
    Optional<CandidateAppointmentRequestEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    List<CandidateAppointmentRequestEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    @Query(value = """
                select count(x) from CandidateAppointmentRequestEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);


    List<CandidateAppointmentRequestEntity> findAllByCandidateIdAndActiveFlag(Long candidateId, String activeFlag);

    Optional<CandidateAppointmentRequestEntity> findByIdAndCandidateIdAndActiveFlag(Long requestId,
                                                                                    Long candidateId, String activeFlag);

    @Query(value = """
        select car from CandidateAppointmentRequestEntity car where car.candidateId = :candidateId and car.activeFlag = :activeFlag
        order by car.createdAt limit 1
    """)
    Optional<CandidateAppointmentRequestEntity> getCandidateAppointmentRequestDetails(Long candidateId, String activeFlag);

    @Transactional
    @Modifying
    @Query(value = """
        update CandidateAppointmentRequestEntity care
            set care.approvalStatus = 'Cancelled',
                care.statusNotes = :statusNotes where care.candidateId = :applicationId and care.stayFrom <= cast(:appointmentTo as date)
                    and cast(:appointmentFrom as date) <= care.stayTo and care.id <> :requestId and care.approvalStatus in ('Validating','Pending')
    """)
    void cancelOverlappingRequests(Long requestId, Long applicationId, String appointmentFrom,
                                   String appointmentTo, String statusNotes);


    @Query(value = """
            select * from schooldev.search_candidates_hostel(:validationStatus,:category,:appointmentFromDate,
                        :appointmentToDate,:stayFrom,:stayTo,:candidateName,:candidateId,:validatorName,
                                    :validatorEmail,:stayType,:tabNo,:userRole,:submittedFromDate,:submittedToDate,:approvalFromDate,
                                                :approvalToDate,:hostelId,:loginUserName,:candidateEmail,:activeFlag) as result"""
            , nativeQuery = true)
    Object[] getOtherCandidateRequests(String validationStatus, String category, String appointmentFromDate, String appointmentToDate,
            String stayFrom, String stayTo, String candidateName, String candidateId, String validatorName,
            String validatorEmail, String stayType, Integer tabNo, String userRole,
            String submittedFromDate, String submittedToDate, String approvalFromDate, String approvalToDate,
            Integer hostelId, String loginUserName, String candidateEmail, String activeFlag);


    @Transactional
    @Modifying
    @Query(value = """
        update CandidateAppointmentRequestEntity care
            set care.approvalStatus = 'Deleted',
                care.statusNotes = :statusNotes where care.candidateId = :candidateId and care.id = :requestId
    """)
    int deleteAppointmentRequests(Long requestId, Long candidateId, String statusNotes);

    @Query(value = """
        select care from CandidateAppointmentRequestEntity care join CandidateWorkflowEntity cwe on 
            (care.id = cwe.applicationId) where cwe.id = :workflowId 
                AND (LOWER(cwe.authorityType) NOT LIKE LOWER(CONCAT('%', :ccw, '%')) AND LOWER(cwe.authorityType) NOT LIKE LOWER(CONCAT('%', :dean, '%')))
                and care.approvalStatus = :approvalStatus
    """)
    Optional<CandidateAppointmentRequestEntity> getCandidateRequestByStatus(Long workflowId, String approvalStatus,String ccw, String dean);

    @Query(value = """
        select care.approvalStatus from CandidateAppointmentRequestEntity care where care.id = 
            (select cwe.applicationId from CandidateWorkflowEntity cwe where cwe.id = :workflowId) 
                and care.approvalStatus not in ('Cancelled','Deleted') and care.activeFlag = :activeFlag
    """)
    Optional<String> getApprovalStatus(Long workflowId, LocalDateTime modifiedAt, String activeFlag);

    @Query(value = """
            select new com.iitm.hosteldine.service.OtherCandidate.RequestDetails(
                csre.stayId,csre.stayFrom,csre.stayTo,care.stayFrom,care.stayTo,care.id,cre.email,cre.gender,hri.id,hri.building.hostel.id)
                from CandidateAppointmentRequestEntity care join CandidateProfileEntity cre 
                on (care.candidateId = cre.id) left join CandidateStayRequestEntity csre on (care.candidateId = csre.candidateId
                    and csre.stayId = :stayId) join HostelRoomInfoEntity hri on(hri.roomNo = :roomNo and hri.activeFlag=:activeFlag)
                        join HostelFloorMasterEntity hfm on (hfm.id = hri.building.id and hfm.activeFlag = :activeFlag)
                            where hfm.hostel.id = :hostelId and hri.roomNo = :roomNo and care.id = :requestId 
                                and ((:stayId > 0 and csre.approvalStatus = :approvalStatus)
                                    or (:stayId <= 0 and care.approvalStatus = :approvalStatus)
                                    )
            """)
    Optional<RequestDetails> getDetailsByRequestIdAndApprovalStatus(Long requestId, Long stayId, Long hostelId, String roomNo,
                                                                    String approvalStatus, String activeFlag);

    List<CandidateAppointmentRequestEntity> findAllByCandidateIdAndApprovalStatusAndActiveFlag(Long candidateId, String approvalStatus, String activeFlag);
}