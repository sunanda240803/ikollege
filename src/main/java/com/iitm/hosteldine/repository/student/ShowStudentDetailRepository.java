package com.iitm.hosteldine.repository.student;

import com.iitm.hosteldine.model.student.ShowStudentDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowStudentDetailRepository extends JpaRepository<ShowStudentDetailEntity, Long> {

    List<ShowStudentDetailEntity> findAllByActiveFlagAndSeatIdAndStudentIdIgnoreCase(String activeFlag, Long seatId, String vsdiStudentid);
    Long countAllByActiveFlagAndSeatIdAndStudentId(String activeFlag,Long seatId,String studentId);

    @Query(value = """
            select * from schooldev."COMPLETE_STUDENT_VIEW" WHERE n_fm_facility_master_name is not null
            and n_fm_facility_master_id is not null and upper(V_SDI_STUDENTID)=upper(:studentId)
            """,nativeQuery = true)
    List<Object[]> checkIsStudentDayShcolarOrHostler(String studentId);

    @Query(value = "SELECT schooldev.student_balance(:studentId) AS balance", nativeQuery = true)
    Double checkStudentBalance(@Param("studentId") String studentId);

    @Query(value = """
            select sum(discount_amount * ssd.purchased_count) as total_purchase
            from schooldev."SHOW_STUDENT_DETAILS" ssd
            join schooldev."SHOW_SEAT_DETAILS" ssed on (ssd.seat_id = ssed.id)
            join schooldev."SHOW_MASTER" sm on (sm.id = ssed.show_id)
            join schooldev."SHOW_EVENT_MASTER" sem on (sem.id = sm.event_id)
            where upper(ssd.student_id) = upper(:studentId) and sem.id = :eventId and ssd.active_flag = :activeFlag 
            group by ssd.student_id
            """, nativeQuery = true)
    List<Object[]> getStudentLimit(String studentId,Long eventId,String activeFlag);

    @Query(value = """
        select ssd from ShowStudentDetailEntity ssd where ssd.studentId = upper(:studentId)
                and ssd.seatId in (select ssed.id from ShowSeatDetailsEntity ssed where ssed.show.id = :showId and ssed.activeFlag=:activeFlag)
                        and ssd.activeFlag = :activeFlag
        """)
    List<ShowStudentDetailEntity> getByStudentDetailsByShow(String studentId,Long showId,String activeFlag);

    Optional<List<ShowStudentDetailEntity>> findAllByStudentIdAndActiveFlag(String studentId, String activeFlag);
    boolean  existsByActiveFlagAndSeatIdAndStudentIdIgnoreCase(String activeFlag,Long seatId,String studentId);

    @Query(value = """
            select v.* from schooldev.event_purcharse_details_view v 
             where v_sdi_studentid= :studentId 
                        order by v.id 
            """, nativeQuery = true)
    List<Object[]> getStudentPurchaseInfo(String studentId);


    @Query(value = """
        SELECT 
            ssd.purchased_count as purchasedCount,
            ssd.student_id as studentId,
            sv.student_name as studentName,
            sv.hostel_name as hostelName,
            sv.room_number as roomNo,
            ssed.seat_name as seatName,
            SUM(ssd.discount_amount) as totalPurchase,
            TO_CHAR(ssd.created_at, 'DD-Mon-YY HH24:MI') as createdAt,
            sm.show_name as showName,
            ssd.student_name as printingName
        FROM 
            schooldev."SHOW_STUDENT_DETAILS" ssd 
        JOIN 
            schooldev."SHOW_SEAT_DETAILS" ssed 
            ON (ssed.id = ssd.seat_id) 
        JOIN 
            schooldev."SHOW_MASTER" sm 
            ON (sm.id = ssed.show_id) 
        LEFT JOIN 
            schooldev."ALL_STUDENTS_DETAILS_VIEW" sv 
            ON (sv.student_id = ssd.student_id) 
        WHERE 
            ssd.active_flag = 'Y'
            AND sm.event_id = :eventId
            AND (:showId IS NULL OR sm.id = :showId)
            AND (:seatId IS NULL OR ssed.id = :seatId)
            AND ssd.created_at::date >= :fromDate
            AND ssd.created_at::date <= :toDate
        GROUP BY 
            ssd.purchased_count, ssd.student_id, sv.student_name, sv.hostel_name, 
            sv.room_number, ssd.created_at, sm.show_name, ssed.seat_name, ssd.student_name
        ORDER BY 
            sv.hostel_name, ssd.student_id
        """, nativeQuery = true)
    List<Object[]> findShowPurchases(
            @Param("eventId") Long eventId,
            @Param("showId") Long showId,
            @Param("seatId") Long seatId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("active") String active);


    @Query(value = """
        SELECT 
            SUM(amount) as claim_amount,
            student_id,
            event_id
        FROM 
            schooldev."SHAASTRA_SAARANG_PURCHASE_CLAIM"
        WHERE 
            event_id = :eventId
        GROUP BY 
            event_id, student_id
        """, nativeQuery = true)
    List<Object[]> findClaimsByEventId(@Param("eventId") Long eventId);
}