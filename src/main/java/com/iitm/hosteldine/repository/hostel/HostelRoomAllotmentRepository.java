package com.iitm.hosteldine.repository.hostel;

import com.iitm.hosteldine.model.hostel.HostelRoomAllotmentInfoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HostelRoomAllotmentRepository extends JpaRepository<HostelRoomAllotmentInfoEntity, Long> {

	@Query("""
		       SELECT ai.assetCode AS assetCode, 
		              ai.assetName AS assetName,
		              ai.assetConditionStatus AS assetConditionStatus,
		              aci.assetCategory AS assetCategory
		       FROM HostelRoomAllotmentInfoEntity hral
		       LEFT JOIN HostelRoomInventoryEntity hr ON hr.room.id = hral.roomId AND hr.activeFlag = 'Y'
		       LEFT JOIN AssetInventoryInfoEntity ai ON CAST(ai.assetId AS string) = hr.itemId AND ai.activeFlag = 'Y'
		       LEFT JOIN AssetCategoryInfoEntity aci ON ai.assetCategoryId = aci.assetCategoryId
		       WHERE hral.activeFlag = :statusActive
		       AND hral.studentId = :studentId
		       AND (hral.vacateDate IS NULL OR hral.isMissing = true)
		       AND (hral.shiftedDate IS NULL OR hral.isMissing = true)
		""")
		List<Object[]> viewInventory( String statusActive,String studentId);
		
		@Query(value = """
				SELECT v_sdi_studentid as studentId, hri.room_No as roomNo, s.studentname as studentName,
				hfm.floor_name as floorName, v_sdi_gender as gender, split_part(hfm.floor_name, ' ', 1) as hostelFirstName,
				hfm.hostel_id as hostelId
				FROM schooldev."HOSTEL_ROOM_ALLOTMENT_INFO" hral, schooldev.all_student_view s,
				schooldev."HOSTEL_ROOM_INFO" hri
				join schooldev."HOSTEL_FLOOR_MASTER" hfm on (hfm.floor_id = hri.building_id)
				WHERE hral.active_flag = 'Y' and hral.vacate_date is null and hral.shifted_date is null AND
				hral.student_id = v_sdi_studentid AND hri.room_id= hral.room_id and  hri.active_flag=:statusActive
				and v_sdi_studentid = :studentId
			""", nativeQuery = true)
	List<Object[]> getHostelDetailsByStudentId(String statusActive, String studentId);
	
	@Query(value = """
		    SELECT count(*) FROM schooldev.room_occupancy_status(:currentDate, :nextYearDate, :hostelName, :roomNo, :seat)
		    """, nativeQuery = true)
	Integer getRoomOccupancyStatus(LocalDate currentDate, LocalDate nextYearDate, Long hostelName,
			Long roomNo, String seat);
	
	@Query(value = "SELECT h.studentId, h.roomAllotmentId, "
	        + " CASE WHEN FUNCTION('DATE', h.stayFromDate) = CURRENT_DATE THEN 'updated' ELSE 'insert' END "
	        + " FROM HostelRoomAllotmentInfoEntity h WHERE UPPER(h.studentId) = UPPER(:studentId) "
	        + " AND h.activeFlag = :activeFlag AND h.vacateDate IS NULL AND h.shiftedDate IS NULL")
	Object[] checkAllomentInsertOrUpdate(String studentId, String activeFlag);

	@Modifying
	@Query("UPDATE HostelRoomAllotmentInfoEntity h SET h.buildingId = :buildingId, h.roomId = :roomId,"
			+ "	h.subRoomId = :subRoomId, h.modifiedBy = :userId, h.modifiedAt = :now"
			+ " WHERE UPPER(h.studentId) = UPPER(:studentId) AND h.activeFlag = :activeFlag"
			+ " AND (h.vacateDate IS NULL AND h.shiftedDate IS NULL)")
	void updateHostelRoomAllotmentInfoStatus(Long buildingId, Long roomId, String subRoomId, String userId,
			LocalDateTime now, String studentId, String activeFlag);


	
	@Query(value = "SELECT NEXTVAL('schooldev.\"SEQ_MAST_HOSTEL_ROOM_ALLOTMENT_ID\"')")
	Long getNextSequenceRoomAllotmentId();
	
	@Query(value = "SELECT DISTINCT h.subRoomId FROM HostelRoomAllotmentInfoEntity h " +
            "WHERE h.subRoomId IS NOT NULL AND h.subRoomId != '' ORDER BY h.subRoomId")
	List<String> findDistinctSeats();

	Optional<HostelRoomAllotmentInfoEntity> findByStudentIdEqualsIgnoreCase(String studentId);

	Optional<HostelRoomAllotmentInfoEntity> findByStudentIdEqualsIgnoreCaseAndActiveFlagAndVacateDateIsNullAndShiftedDateIsNull(
																			String studentId, String activeFlag);


	@Query(value = """
	select warden_email,v_fpd_email_address,n_fm_facility_master_name,v_sdi_studentid,ldap_username as warden_name,v_um_username as hostel_office_name
		from schooldev."COMPLETE_STUDENT_VIEW" b join schooldev."HOSTEL_USER_MAPPING" c
		on ((c.hostel_id=b.n_fm_facility_master_id) and  c.active_flag=:activeFlag) join schooldev."USER_MANAGEMENT" h
		on ((c.user_name=h.user_name) and h.v_um_active_flag=:activeFlag) join schooldev."WARDEN_HOSTEL_MAPPING" whm
		on(whm.hostel_id=b.n_fm_facility_master_id and whm.active_flag=:activeFlag) join schooldev."WARDEN_INFO" wi
		on (whm.warden_id=wi.id  and wi.active_flag=:activeFlag) left join schooldev."FACULTY_PERSONAL_DETAILS" fpd
		on ((h.user_id=j.faculty_id) and j.active_flag=:activeFlag)
	""",nativeQuery = true)
	Optional<List<Object[]>> getWardenAndFacultyEmails(String studentId,String activeFlag);

	@Query(value = """
			select * from schooldev.room_occupancy_status(:fromDate,:toDate,:hostelId,:roomId,:subRoomId)
			""", nativeQuery = true)
	Optional<List<Object[]>> getAllottedDetails(LocalDate fromDate, LocalDate toDate, Long hostelId, Long roomId, String subRoomId);

	Optional<HostelRoomAllotmentInfoEntity> findByRoomAllotmentId(Long roomAllotmentId);

	Optional<HostelRoomAllotmentInfoEntity> findByRoomAllotmentIdAndStudentIdAndActiveFlag(Long roomAllotmentId,String studentId,String activeFlag);

	boolean existsByActiveFlagAndRoomId(String activeFlag, Long roomId);
	
	@Query(value = """
			  SELECT
			    student_type, allocation_type, student_id, student_name, email, prev_hostel, current_hostel, prev_room, current_room,
			    created_by, created_at, shifted_date, vacatedate, stay_from_date, stay_to_date
			  	FROM schooldev.search_room_allotment_logs(CAST(:hostelId AS INTEGER), CAST(:fromDate AS VARCHAR), CAST(:toDate AS VARCHAR),
			    CAST(:createdUser AS VARCHAR), CAST(:allocationType AS VARCHAR),
			    CAST(:studentId AS VARCHAR), CAST(:studentEmail AS VARCHAR)
			  )
			""", nativeQuery = true)
	Page<Object[]> getRoomAllotmentLogsList(int hostelId, LocalDate fromDate, LocalDate toDate, String createdUser, String allocationType, String studentId, String studentEmail, Pageable pageable);
  
	@Query(value = """ 
			  select created_by from schooldev."COMPLETE_HOSTEL_ALLOTMENT_VIEW" group by created_by			 
			""", nativeQuery = true)
	List<String> getUsersList();
	
	boolean existsByStudentIdAndActiveFlag(String studentId, String activeFlag);
	
	static final String regularStudentCheckInQuery = "SELECT cc.allotment_id, cc.hostel_name, cc.room_no, "
			+ "cc.sub_room_id, cc.student_name, cc.student_id, cc.vacate_date, cc.shifted_date, cc.allocation_status, "
			+ "b.approval_date, cc.checked_in_date, vmc.vacation_checkout_status, b.hostel_or_warden_approval_status, vs.student_id,"
			+ "cc.stay_from_date, cc.stay_to_date "
			+ "FROM schooldev.\"COMPLETE_HOSTEL_ALLOTMENT_VIEW\" AS cc "
			+ "LEFT JOIN schooldev.\"IITMSTUDENT_HOSTEL_ROOM_VACATING_REQUEST\" AS b ON "
			+ "(cc.student_id = b.student_id AND b.hostel_or_warden_approval_status = 'Approved' "
			+ "AND b.approval_date IS NOT NULL AND b.active_flag = 'Y' AND b.rejoining_date IS NULL) "
			+ "LEFT JOIN schooldev.\"VACATION_MESS_ALLOWED_STUDENT\" AS vs ON "
			+ "(cc.student_id = vs.student_id AND vs.active_flag = 'Y') "
			+ "LEFT JOIN schooldev.\"VACATION_MESS_CONTROLLER\" vmc ON "
			+ "(vmc.active_flag='Y' AND vmc.vacation_checkout_status = true "
			+ "AND now()::date >= vacation_checkout_start_date::date) "
			+ "WHERE allocation_type = 'N' AND hostel_id = :hostelId AND cc.active_flag = 'Y' "
			+ "AND ((vacate_date IS NULL AND shifted_date IS NULL AND allocation_status IS NULL) "
			+ "OR ((vacate_date  IS NULL) AND (shifted_date IS NULL OR shifted_date IS NOT NULL) "
			+ "AND allocation_status = 'CheckedIn')) ";
	static final String regularStudentWhereClause = "AND LOWER(cc.student_id) LIKE LOWER(CONCAT('%', :studentId, '%')) ";
	static final String regularStudentWhereDates = "AND (cc.modified_at > (NOW() - INTERVAL '1800' DAY )) AND stay_from_date <= now()::date ";
	static final String regularStudentFromDate = "AND cc.modified_at >= CAST(:fromDate AS timestamp) ";
	static final String regularStudentToDate = "AND cc.modified_at <= CAST(:todate AS timestamp) ";
	static final String regularStudentOrderBy = "ORDER BY allocation_status";

	@Query(value = regularStudentCheckInQuery + regularStudentWhereClause + regularStudentOrderBy, nativeQuery = true)
	Object[] getHotelAllotmentList(@Param("hostelId") Integer hostelId, @Param("studentId") String studentId);

	@Query(value = regularStudentCheckInQuery + regularStudentWhereDates + regularStudentOrderBy, nativeQuery = true)
	Object[] getHotelAllotmentList(@Param("hostelId") Integer hostelId);

	@Query(value = regularStudentCheckInQuery + regularStudentFromDate + regularStudentOrderBy, nativeQuery = true)
	Object[] getHotelAllotmentListByFromDate(@Param("hostelId") Integer hostelId,
			@Param("fromDate") LocalDate fromDate);

	@Query(value = regularStudentCheckInQuery + regularStudentToDate + regularStudentOrderBy, nativeQuery = true)
	Object[] getHotelAllotmentListByToDate(@Param("hostelId") Integer hostelId,
			@Param("todate") LocalDate todate);

	@Query(value = regularStudentCheckInQuery + regularStudentFromDate + regularStudentToDate
			+ regularStudentOrderBy, nativeQuery = true)
	Object[] getHotelAllotmentListFilterDates(@Param("hostelId") Integer hostelId,
			@Param("fromDate") LocalDate fromDate, @Param("todate") LocalDate todate);

	@Query(value = """
			SELECT hostel_id, hostel_name,sum(total) AS total_no_of_seats ,sum(occupied) AS alloted,
			SUM(CASE WHEN vacancy<0 THEN 0 ELSE vacancy END) AS exact_vacancy,
			COUNT(DISTINCT(room_no)) AS total_rooms, SUM(CASE WHEN vacancy <= 0 THEN 1 ELSE 0 END) AS rooms_alloted,
			SUM(CASE WHEN vacancy >= 1 THEN 1 ELSE 0 END) AS rooms_free
			FROM (SELECT * FROM schooldev."ROOM_OCCUPANCY_STATUS_VIEW") AS occupancy_status
			WHERE hostel_id = :hostelId GROUP BY hostel_id, hostel_name ORDER BY hostel_name
			""", nativeQuery = true)
	List<Object[]> getOccupancyCount(@Param("hostelId") Integer hostelId);

	@Query(value = """
			SELECT SUM(CASE WHEN shifted_date IS NULL THEN 1 ELSE 0 END) AS occupied, 
			SUM(CASE WHEN allocation_status = 'CheckedIn' THEN 1 ELSE 0 END ) AS check_in_count,
			SUM(CASE WHEN allocation_status = 'CheckedOut' THEN 1 ELSE 0 END ) AS check_out_count,
			SUM(CASE WHEN shifted_date IS NOT NULL THEN 1 WHEN vacate_date IS NOT NULL THEN 1 END) AS vacated_count
			FROM schooldev."COMPLETE_HOSTEL_ALLOTMENT_VIEW"
			WHERE modified_at::date >= now()::date - interval '1 week' AND hostel_id = :hostelId
						""", nativeQuery = true)
	List<Object[]> getCheckInAndOutCount(@Param("hostelId") Integer hostelId);

	static final String studentStatusWhereClause1 = "WHERE h.studentId = :studentId AND h.roomAllotmentId = :allotmentId AND "
			+ "h.activeFlag = :activeflag AND h.vacateDate IS NULL AND h.shiftedDate IS NULL";

	static final String studentStatusWhereClause2 = "WHERE h.studentId = :studentId AND h.roomAllotmentId = :allotmentId AND "
			+ "h.activeFlag = :activeflag AND h.status = :status";
	
	@Modifying
	@Query("UPDATE HostelRoomAllotmentInfoEntity h SET h.status = :status, h.checkedInDate = :now, h.modifiedBy = :modifiedBy, "
			+ "h.modifiedAt = :modifiedAt " + studentStatusWhereClause1)
	int updateStudentCheckInStatus(String status, LocalDateTime now, String studentId, Long allotmentId,
			String activeflag, String modifiedBy, LocalDateTime modifiedAt);
	    
	@Modifying
	@Query("UPDATE HostelRoomAllotmentInfoEntity h SET h.vacationCheckoutStatus = :status, h.vacationCheckoutDate = :now, "
			+ "h.modifiedBy = :modifiedBy, h.modifiedAt = :modifiedAt " + studentStatusWhereClause1)
	int updateStudentVacationCheckOutStatus(String status, LocalDate now, String studentId, Long allotmentId,
			String activeflag, String modifiedBy, LocalDateTime modifiedAt);

	@Modifying
	@Query("UPDATE HostelRoomAllotmentInfoEntity h SET h.status = :updateStatus, h.vacateDate = :vacateDate, h.checkedOutDate = :checkedOutDate, "
			+ "h.modifiedBy = :modifiedBy, h.modifiedAt = :modifiedAt " + studentStatusWhereClause2)
	int updateStudentVacateCheckOutStatus(String updateStatus, LocalDate vacateDate, LocalDateTime checkedOutDate,
			String studentId, Long allotmentId, String activeflag, String status, String modifiedBy, LocalDateTime modifiedAt);

	@Modifying
	@Query("UPDATE HostelRoomAllotmentInfoEntity h SET h.status = :updateStatus, h.checkedOutDate = :checkedOutDate, "
			+ "h.modifiedBy = :modifiedBy, h.modifiedAt = :modifiedAt " + studentStatusWhereClause2)
	int updateStudentVacateCheckOutStatus(String updateStatus, LocalDateTime checkedOutDate, String studentId,
			Long allotmentId, String activeflag, String status, String modifiedBy, LocalDateTime modifiedAt);


	@Modifying
	@Query(value = """
	update HostelRoomAllotmentInfoEntity h set h.modifiedBy = :modifiedBy, h.activeFlag = 'N', h.modifiedAt = :modifiedAt
		where h.studentId = :studentId and h.activeFlag = :activeFlag and (h.vacateDate is null and h.shiftedDate is null )
	""")
	int deactivateExistingStudentHostelRoom(String studentId, String modifiedBy, LocalDateTime modifiedAt, String activeFlag);

}