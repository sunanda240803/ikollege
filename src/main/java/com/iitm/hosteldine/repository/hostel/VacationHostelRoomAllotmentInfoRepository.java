package com.iitm.hosteldine.repository.hostel;

import com.iitm.hosteldine.model.hostel.VacationHostelRoomAllotmentInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface VacationHostelRoomAllotmentInfoRepository extends JpaRepository<VacationHostelRoomAllotmentInfoEntity, Long> {

    Optional<VacationHostelRoomAllotmentInfoEntity> findByIdAndActiveFlag(Long roomAllotmentId, String activeFlag);

    @Query("""
        SELECT vhrai FROM VacationHostelRoomAllotmentInfoEntity vhrai
            WHERE vhrai.stayFromDate = :fromDate AND vhrai.stayToDate = :toDate
            AND vhrai.activeFlag = :activeFlag 
            AND UPPER(vhrai.studentId) = UPPER(:studentId)
    """)
    Optional<VacationHostelRoomAllotmentInfoEntity> getAllotStudentDetails(String studentId, LocalDate fromDate, LocalDate toDate, String activeFlag);

    @Query(value = """
    select vhrai from VacationHostelRoomAllotmentInfoEntity vhrai where vhrai.stayFromDate = :stayFrom and 
        vhrai.stayToDate = :stayTo and vhrai.email = :email and vhrai.activeFlag = :activeFlag and 
            vhrai.requestid = :requestId
    """)
    Optional<VacationHostelRoomAllotmentInfoEntity> getAllocatedDetailsByRequestId(LocalDate stayFrom, LocalDate stayTo,
                                                                                   String email,Long requestId,String activeFlag);
}