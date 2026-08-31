package com.iitm.hosteldine.repository.dashboard.student;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.iitm.hosteldine.service.reports.VacatingStudentReportRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import com.iitm.hosteldine.model.dashboard.student.StudentHostelRoomVacatingRequestEntity;

public interface StudentHostelRoomVacatingRequestRepository extends JpaRepository<StudentHostelRoomVacatingRequestEntity, Long> {

    @Query("select sse from StudentHostelRoomVacatingRequestEntity sse " +
            "where sse.student = :student and sse.activeFlag = :activeFlag and sse.rejoiningDate is null  order by sse.createdAt desc limit 1")
    Optional<StudentHostelRoomVacatingRequestEntity> getStudentVacatingDetails(StudentDetailsInfoEntity student, String activeFlag);

	Optional<StudentHostelRoomVacatingRequestEntity>
	findFirstByStudentAndActiveFlagAndRejoiningDateIsNullOrderByCreatedAtDesc(
			StudentDetailsInfoEntity student,
			String activeFlag);

    @Modifying
    @Query("UPDATE StudentDetailsInfoEntity w SET w.dayScholar = :dayScholar " +
            "WHERE w.studentId = :studentId and w.activeFlag = :activeStatus ")
    void updateStudentInfoDetails(String studentId, String activeStatus, String dayScholar);

    @Query(value = """
    SELECT * FROM schooldev.student_vacating_hostel(
        CAST(:submittedFromDate AS VARCHAR), CAST(:submittedToDate AS VARCHAR), CAST(:vacatingReason AS VARCHAR),
        CAST(:vacatingFromDate AS VARCHAR), CAST(:vacatingToDate AS VARCHAR), CAST(:hostelId AS INT),
        CAST(:studentName AS VARCHAR), CAST(:studentId AS VARCHAR), CAST(:wardenApprovalStatus AS VARCHAR),
        CAST(:userRole AS VARCHAR), CAST(:approvalLevel AS INT), CAST(:approvalEmail AS VARCHAR), CAST(:username AS VARCHAR)
    )
    """, nativeQuery = true)
    Object[] getStudentVacatingHostelList(
    		LocalDate submittedFromDate,
    		LocalDate submittedToDate,
            String vacatingReason,
            LocalDate vacatingFromDate,
            LocalDate vacatingToDate,
            Integer hostelId,
            String studentName,
            String studentId,
            String wardenApprovalStatus,
            String userRole,
            Integer approvalLevel,
            String approvalEmail,
            String username
    );
    
    @Query(value = """
			select a.student_id, (v_sdi_firstname||''||v_sdi_lastname) as studentname, b.v_sdi_gender,c.dob,a.id,
			a.hostel_or_warden_approval_status,b.v_hri_roomno,b.n_fm_facility_master_name,
			a.vacating_date,a.vacating_reason,a.mobile_no,a.email_id,a.recommended_by ,a.checked_by,a.employee_id,b.n_hri_roomid,
			a.exchange_prog_period_from_date,a.exchange_prog_period_to_date,a.donation_amount,a.donator_type,a.penality_amount,a.penalty_reason,a.room_painting_type,v_sdi_studentaddress
			from schooldev."IITMSTUDENT_HOSTEL_ROOM_VACATING_REQUEST_VIEW" a 
			join schooldev."VACATED_STUDENT_VIEW"  b on (v_sdi_studentid=a.student_id) 
			join schooldev."ALL_STUDENTS_DETAILS_VIEW" c on (c.student_id=a.student_id)
			where UPPER(TRIM(a.student_id)) = UPPER(TRIM(:studentId))
			AND a.id=:requestId 
			AND a.school_id=1 
			AND a.active_flag='Y'
						""", nativeQuery = true)
	Object[] getVacatingStudentDetails(String studentId, Long requestId);
    
    @Query(value = """
			select b.asset_name ,c.asset_category ,b.asset_id,a.asset_code,e.student_id,n_fm_facility_master_name,
			d.asset_condition,penalty_amount,d.penalty_reason,v_sdi_firstname||''||v_sdi_lastname as student_name 
			from schooldev."IITMSTUDENT_HOSTEL_ROOM_VACATING_REQUEST_VIEW" e 
			JOIN SCHOOLDEV."VACATED_STUDENT_VIEW" ON(STUDENT_ID=V_SDI_STUDENTID and is_missing=false) 
			JOIN SCHOOLDEV."HOSTEL_ROOM_INVENTORY" a ON(room_id=n_HRI_ROOMID and a.active_flag='Y')
			join SCHOOLDEV."ASSET_INVENTORY_INFO" b on(CAST(a.item_id AS BIGINT) = asset_id ) 
			join schooldev."ASSET_CATEGORY_INFO" c on (c.asset_category_id=b.asset_category_id) 
			left join  schooldev."IITM_STUDENT_ROOM_ASSET_DETAILS" d 
			on(b.asset_id::int=d.asset_id::int and e.id=d.vacating_request_id)
			where
			UPPER(TRIM(e.student_id)) = UPPER(TRIM(:studentId)) 
			and n_HRI_ROOMID = :roomId 
			and e.id= :requestId
			and (a.asset_condition = 'Good' or a.asset_condition is not null)
						""", nativeQuery = true)
	Object[] getVacatingStudentInventoryDetails(String studentId, Long requestId, int roomId);
    
    StudentHostelRoomVacatingRequestEntity findByIdAndActiveFlag(Long id, String activeFlag);
    
	@Query(value = """
			SELECT id,vacating_date FROM schooldev."IITMSTUDENT_HOSTEL_ROOM_VACATING_REQUEST_VIEW"
			WHERE student_id = :studentId AND hostel_or_warden_approval_status = :status 
			AND active_flag = :activeFlag AND vacating_date IS NOT NULL
			ORDER BY id DESC LIMIT 1
						""", nativeQuery = true)
	List<Object[]> getHostelRoomVacatingView(String studentId, String status, String activeFlag);

	@Query(value = """
		select new com.iitm.hosteldine.service.reports.VacatingStudentReportRecord(
			 d.studentName, a.acountName, a.student.studentId,
			 b.creditAmt, b.debitAmt, b.netBal, a.bankNameOne, a.branchNameOne,
			 a.bankAccountNoOne, a.ifsCodeOne, a.donationStatus, a.donationAmount,
			 a.penalityAmount, a.vacatingReason, a.othersVacatingReason, a.vacatingDate,
			 a.studentAddress, a.exchangeProgPeriodFromDate, a.exchangeProgPeriodToDate,
			 a.donatorType, a.othersDescription, a.placeOfVisit, null, null, null, null, null, null, null)
		 from StudentHostelRoomVacatingRequestEntity a
			 join StudentsLedgerBalanceEntity b on b.acchead = a.student.studentId
			 left join FinalOccupiedRoomIdViewEntity c on c.vHarlStudentid = a.student.studentId
			 join CompleteHostelAllotmentViewEntity d on d.allotmentId = c.nHralRoomallotmentid
			 join SettlementHistoryEntity e on e.studentId = a.student.studentId and e.activeFlag = :activeFlag
		 where  e.settlementDate >= COALESCE(:fromDate, e.settlementDate)
			   AND e.settlementDate <= COALESCE(:toDate, e.settlementDate)
	""")
	List<VacatingStudentReportRecord> getVacatingStudentReport(LocalDate fromDate, LocalDate toDate, String activeFlag);

	@Query(value = """
		select new com.iitm.hosteldine.service.reports.VacatingStudentReportRecord(
			 null, null, a.student.studentId, null, null, null, null, null, null, null, null, null, a.penalityAmount, null,
				 null, null, null, null, null, a.donatorType, null, null, a.hostelOrWardenName, null, null, null, null, null, a.penaltyReason)
		 from StudentHostelRoomVacatingRequestEntity a
			 left join FinalOccupiedRoomIdViewEntity b on b.vHarlStudentid = a.student.studentId
			 join CompleteHostelAllotmentViewEntity c on c.allotmentId = b.nHralRoomallotmentid
			 join SettlementHistoryEntity d on d.studentId = a.student.studentId and d.activeFlag = :activeFlag
		 where a.hostelOrWardenApprovalStatus = :approvalStatus
			 and a.penalityAmount <> 0 and a.activeFlag = :activeFlag
			 and d.settlementDate >= coalesce(:fromDate, d.settlementDate)
			 and d.settlementDate <= coalesce(:toDate, d.settlementDate)
	""")
	List<VacatingStudentReportRecord> getPenaltyReport(LocalDate fromDate, LocalDate toDate, String activeFlag, String approvalStatus);

	@Query(value = """
		select new com.iitm.hosteldine.service.reports.VacatingStudentReportRecord(
			 null, null, a.student.studentId, null, null, null, null, null, null, null, null, a.donationAmount,
			 null, null, null, null, null, null, null, a.donatorType, null, null, a.hostelOrWardenName, a.donatedHostel, null, null, null, null, null)
		 from StudentHostelRoomVacatingRequestEntity a
			 join StudentsLedgerBalanceEntity b on b.acchead = a.student.studentId
			 left join FinalOccupiedRoomIdViewEntity c on c.vHarlStudentid = a.student.studentId
			 join CompleteHostelAllotmentViewEntity d on d.allotmentId = c.nHralRoomallotmentid
			 join SettlementHistoryEntity e on e.studentId = a.student.studentId and e.activeFlag = :activeFlag
		 where a.hostelOrWardenApprovalStatus = :approvalStatus
		 and a.donationStatus = :donationStatus and a.activeFlag = :activeFlag
		 and e.settlementDate >= COALESCE(:fromDate, e.settlementDate)
		 and e.settlementDate <= COALESCE(:toDate, e.settlementDate)
	""")
	List<VacatingStudentReportRecord> getDonationReport(LocalDate fromDate, LocalDate toDate, String activeFlag, String approvalStatus, Boolean donationStatus);

	@Query(value = """
		select new com.iitm.hosteldine.service.reports.VacatingStudentReportRecord(
			 null, a.acountName, a.student.studentId,
			 c.creditAmt, c.debitAmt, c.netBal, a.bankNameOne, a.branchNameOne,
			 a.bankAccountNoOne, a.ifsCodeOne, null, null, null, null, null, a.vacatingDate,
			 a.studentAddress, a.exchangeProgPeriodFromDate, a.exchangeProgPeriodToDate,
			 a.donatorType, a.othersDescription, a.placeOfVisit, null, null, b.settlementFlag, c.acchead, a.mobileNo, d.settlementDate, null)
		 from StudentHostelRoomVacatingRequestEntity a
			 join StudentDetailsInfoEntity b on b.studentId = a.student.studentId and b.settlementFlag = :activeFlag and b.activeFlag = :activeFlag
			 join StudentsLedgerBalanceEntity c on c.acchead = a.student.studentId
			 join SettlementHistoryEntity d on d.studentId = a.student.studentId and d.activeFlag = :activeFlag
		 where a.activeFlag = :activeFlag and a.hostelOrWardenApprovalStatus='Approved' 
			 	and d.settlementDate >= COALESCE(:fromDate, d.settlementDate)
			    and d.settlementDate <= COALESCE(:toDate, d.settlementDate)
			group by a.acountName, a.student.studentId,
				c.creditAmt, c.debitAmt, c.netBal, a.bankNameOne, a.branchNameOne,
				a.bankAccountNoOne, a.ifsCodeOne, a.vacatingDate, a.studentAddress,
				a.exchangeProgPeriodFromDate, a.exchangeProgPeriodToDate, a.donatorType,
				a.othersDescription, a.placeOfVisit, b.settlementFlag, c.acchead, a.mobileNo, d.settlementDate
		 order by a.vacatingDate
	""")
	List<VacatingStudentReportRecord> getVacatingStudentSummaryReport(LocalDate fromDate, LocalDate toDate, String activeFlag);

	@Modifying
	@Query("UPDATE StudentHostelRoomVacatingRequestEntity SET modifiedBy = :userId,modifiedAt = :modifiedAt, rejoiningDate = :nowDate "
			+ "where id= :vacatingId")
	int updateRejoiningDate(String userId,LocalDateTime modifiedAt, LocalDate nowDate,Long vacatingId);

	@Query("SELECT s.student FROM StudentHostelRoomVacatingRequestEntity s WHERE s.vacatingDate = :yesterday AND s.hostelOrWardenApprovalStatus IN (:statuses) AND s.activeFlag = :activeFlag AND s.rejoiningDate IS NULL")
	List<StudentDetailsInfoEntity> findStudentsVacatedYesterday(LocalDate yesterday, List<String> statuses, String activeFlag);
}
