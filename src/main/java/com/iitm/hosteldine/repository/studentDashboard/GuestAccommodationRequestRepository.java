package com.iitm.hosteldine.repository.studentDashboard;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.student.StudentAccoDetailsDTO;
import com.iitm.hosteldine.dto.student.StudentRoomDTO;
import com.iitm.hosteldine.model.studentDashboard.GuestAccommodationRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GuestAccommodationRequestRepository extends JpaRepository<GuestAccommodationRequestEntity, Long> {
    Optional<GuestAccommodationRequestEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    List<GuestAccommodationRequestEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    @Query(value = """
                select count(x) from GuestAccommodationRequestEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);
    
    
    String BASE_QUERY = "FROM schooldev.\"GUEST_ACCOMMODATION_REQUEST\" a"
            + " LEFT JOIN schooldev.guest_app_req_max_stay_id c ON c.req_id = a.id"
            + " LEFT JOIN schooldev.\"GUEST_ACCOMMODATION_REQUEST\" b ON b.id = c.max_stay_extension_id"
            + " WHERE a.active_flag = :statusActive AND a.parent_request_id = 0 AND a.student_id = :studentId";

    @Query(value = """
    	    SELECT a.id,a.student_id AS "studentId", 
    	           CASE WHEN a.parent_request_id = 0 THEN a.id ELSE a.parent_request_id END AS "parentRequestId", 
    	           b.id AS "currentStayExtensionId", 
    	           a.created_at::date AS "createdAt", 
    	           a.from_date AS "fromDate", 
    	           a.to_date AS "toDate", 
    	           CASE WHEN b.warden_approval_status <> 'Cancelled' THEN b.to_date END AS "stayTo", 
    	           CASE WHEN b.id IS NULL THEN a.warden_approval_status ELSE b.warden_approval_status END AS "wardenApprovalStatus", 
    	           CASE WHEN b.id IS NULL THEN a.payment_status ELSE b.payment_status END AS "paymentStatus"
    	    """ + BASE_QUERY, 
        nativeQuery = true)
    
   
	List<Object[]> getAccommodationRequestList(String statusActive, Pageable pageable,
			String studentId);
	
	@Query(value = """
			Select hm.hostel_name as hostelname,wi.id as wardenid,wi.warden_name as wardenname,
			wi.warden_email as wardenemail,wi.alternate_email as alternateemail,wih.id as inchargeid,
			wih.warden_name as inchargename,wih.warden_email as inchargeemail,wih.alternate_email as inchargealternateemail,
			wid.away_from1 ,wid.away_to1,warden1,wid.incharge1,wid.away_from2 , wid.away_to2,warden2,wid.incharge2,
			wid.away_from3 , wid.away_to3,warden3,wid.incharge3,wid.away_from4,wid.away_to4,warden4,wid.incharge4,
			wid.away_from5,wid.away_to5,warden5,wid.incharge5,
			case when (now()::date between wid.away_from1 and wid.away_to1) then
			(case when(now()::date between wid.away_from2 and wid.away_to2) then
			case when (now()::date between wid.away_from3 and wid.away_to3) then
			case when (now()::date between wid.away_from4 and wid.away_to4)  then
			case when (now()::date between wid.away_from5 and wid.away_to5) then  incharge5
			else incharge4 end else incharge3 end else incharge2 end else incharge1 end ) else incharge1 end as incharge,
			case when (now()::date between wid.away_from1 and wid.away_to1) then
			(case when(now()::date between wid.away_from2 and wid.away_to2) then
			case when (now()::date between wid.away_from3 and wid.away_to3) then
			case when (now()::date between wid.away_from4 and wid.away_to4)  then
			case when (now()::date between wid.away_from5 and wid.away_to5) then  5
			else 5 end else 4  end else 3  end else 2  end ) else 1 end as count
			from schooldev."HOSTEL_MASTER" hm
			join schooldev."WARDEN_HOSTEL_MAPPING" wum on(hm.hostel_id = wum.hostel_id)
			left join schooldev.warden_incharge_details_baseview wid on(wum.warden_id = wid.warden1 and now()::date between wid.away_from1 and wid.away_to1)
			left join schooldev."WARDEN_INFO" wi on(wi.id = wum.warden_id)
			left join schooldev."WARDEN_INFO" wih on(wih.id =
			case when (now()::date between wid.away_from1 and wid.away_to1) then
			(case when(now()::date between wid.away_from2 and wid.away_to2) then
			case when (now()::date between wid.away_from3 and wid.away_to3) then
			case when (now()::date between wid.away_from4 and wid.away_to4)  then
			case when (now()::date between wid.away_from5 and wid.away_to5) then  incharge5
			else incharge4 end else incharge3  end else incharge2  end else incharge1  end ) else incharge1 end)
			where wum.hostel_id = :hostelId and wum.active_flag = :statusActive

		""", nativeQuery = true)
	List<Object[]> getWardenDetails(String statusActive, Long hostelId);
	
	
	@Query(value = """
			SELECT  a FROM GuestAccommodationRequestEntity a
			WHERE a.studentDetailsInfo.studentId = :studentId AND (a.id= :requestId or a.parentRequestId=:requestId)  AND a.activeFlag= :statusActive
			AND (a.cancelStatus != :statusActive) ORDER BY a.createdAt
					""")
	List<Object[]> getAccommodationRequestDetailsByRequestId(long requestId, String studentId , String statusActive);
	
	@Query(value = """
			SELECT a FROM GuestAccommodationRequestEntity a
			where a.studentDetailsInfo.studentId = :studentId and (a.id= :requestId or a.parentRequestId=:requestId)  and a.activeFlag = :statusActive 
			order by id desc limit 1
			""")
	Optional<GuestAccommodationRequestEntity> getLatestRequest(String studentId, Long requestId,String statusActive);

	@Query(value = """
			SELECT a FROM GuestAccommodationRequestEntity a
			where a.studentDetailsInfo.studentId = :studentId and a.activeFlag = :statusActive order by id desc limit 1
			""")
	Optional<GuestAccommodationRequestEntity> getLatestRequestByStudentId(String studentId, String statusActive);

	Optional<GuestAccommodationRequestEntity> findByIdAndActiveFlagAndWardenApprovalStatusAndStudentDetailsInfoStudentId(
			Long requestId, String statusActive, String status, String studentId);

	@Query(value = """
			SELECT a FROM GuestAccommodationRequestEntity a
			where a.id= :requestId And a.activeFlag = :statusActive And a.wardenApprovalStatus= :validating And a.studentDetailsInfo.studentId = :studentId 
		""")
	Optional<GuestAccommodationRequestEntity> cancelGuestAccommodationRequestById(Long requestId, String statusActive,
			String validating, String studentId);

	@Query(value = """
			SELECT a FROM GuestAccommodationRequestEntity a
			where a.studentDetailsInfo.studentId = :studentId and (a.id= :requestId or a.parentRequestId=:requestId)
			and a.wardenApprovalStatus NOT IN (:cancelled, :rejected)   
			order by a.createdAt desc limit 1
			""")
	Optional<GuestAccommodationRequestEntity> getPreviousStayPeriods(String studentId, Long requestId, String cancelled, String rejected);
	
	@Query(value = """
	        SELECT count(*)
	        """ + BASE_QUERY, 
	        nativeQuery = true)
	long getAccommodationRequestCount(String statusActive, String studentId);



	@Query(value = "SELECT distinct schooldev.guest_accommodation_request( " +
			"    cast(:approvalfromdate as varchar),  " +
			"    cast(:approvaltodate as varchar),  " +
			"    cast(:submittedfromdate as varchar),  " +
			"    cast(:submittedtodate as varchar),  " +
			"    cast(:hostelid as int4), " +
			"    cast(:paymentfromdate as varchar),  " +
			"    cast(:paymenttodate as varchar), " +
			"    cast(:studentname as varchar),  " +
			"    cast(:studentid as varchar),  " +
			"    cast(:wardenname as varchar),  " +
			"    cast(:wardenemail as varchar),  " +
			"    cast(:wardenapprovalstatus as varchar),  " +
			"    cast(:paymentstatus as varchar),  " +
			"    cast(:staytype as varchar),  " +
			"    cast(:userrole as varchar),  " +
			"    cast(:username as varchar) " +
			");", nativeQuery = true)
	List<StudentRoomDTO> getStudentRoomDetails(
			@Param("approvalfromdate") LocalDate approvalFromDate,
			@Param("approvaltodate") LocalDate approvalToDate,
			@Param("submittedfromdate") LocalDate submittedFromDate,
			@Param("submittedtodate") LocalDate submittedToDate,
			@Param("hostelid") int hostelId,
			@Param("paymentfromdate") LocalDate paymentFromDate,
			@Param("paymenttodate") LocalDate paymentToDate,
			@Param("studentname") String studentFullName,
			@Param("studentid") String studentID,
			@Param("wardenname") String wardenName,
			@Param("wardenemail") String wardenEmail,
			@Param("wardenapprovalstatus") String wardenApprovalStatus,
			@Param("paymentstatus") String paymentStatus,
			@Param("staytype") String stayType,
			@Param("userrole") String userRole,
			@Param("username") String userName);

	@Query(value = " SELECT a.id,a.created_at,a.from_date,a.to_date,a.purpose_of_visit,a.no_of_days,a.no_of_persons,a.warden_email, " +
			" a.warden_name,a.warden_approval_status,a.cancel_status,a.student_id,a.approval_date,a.applicable_charges,a.documents_uploaded, " +
			" accommodation_type, approval_notes,rejection_description,parent_request_id,blood_relation_status,allocation_status,checkin_time,checkout_time, " +
			" ar_name, payment_amount, payment_date, payment_reference_no,a.amount,a.secondary_amount,a.mail_sent_to   " +
			" FROM  "+ModelConstants.SCHEMA+".\"GUEST_ACCOMMODATION_REQUEST\" a" +
			" LEFT JOIN "+ModelConstants.SCHEMA+".\"GUEST_ROOM_ALLOTMENT_INFO_VIEW\" b ON (a.id = b.request_id AND a.student_id = b.student_id)" +
			" LEFT JOIN "+ModelConstants.SCHEMA+".\"HOSTEL_FLOOR_MASTER\" sf ON (sf.floor_id = b.building_id)" +
			" LEFT JOIN "+ModelConstants.SCHEMA+".\"HOSTEL_MASTER\" fm ON (fm.hostel_id = sf.hostel_id)" +
			" LEFT JOIN "+ModelConstants.SCHEMA+".\"HOSTEL_ROOM_INFO\" hri ON (b.room_id = hri.room_id)" +
			" WHERE a.STUDENT_ID = :studentId AND a.ID = :requestId AND a.SCHOOL_ID = 1 AND a.ACTIVE_FLAG = 'Y'", nativeQuery = true)
	List<Object[]> findGuestAccommodationDetails(@Param("studentId") String studentId,@Param("requestId") Integer requestId);

	@Query(value = "SELECT guest_name, relation_of_guest, guest_gender, id_proof, hostel_name, room_no " +
			" FROM "+ModelConstants.SCHEMA+".\"GUEST_ACCOMMODATION_GUEST_DETAILS\" a " +
			" LEFT JOIN "+ModelConstants.SCHEMA+".\"GUEST_ROOM_ALLOTMENT_INFO\" b ON (a.request_id::text = b.requestid::text AND " +
			" (a.guest_id::character varying IN (SELECT regexp_split_to_table(b.guest_id, ',') AS guestids " +
			" FROM "+ModelConstants.SCHEMA+".\"GUEST_ROOM_ALLOTMENT_INFO\")) AND b.active_flag = 'Y') " +
			" LEFT JOIN "+ModelConstants.SCHEMA+".\"HOSTEL_FLOOR_MASTER\" sf ON (sf.floor_id = b.building_id) " +
			" LEFT JOIN "+ModelConstants.SCHEMA+".\"HOSTEL_MASTER\" fm ON (fm.hostel_id = sf.hostel_id) " +
			" LEFT JOIN "+ModelConstants.SCHEMA+".\"HOSTEL_ROOM_INFO\" hri ON (b.roomid = hri.room_id) " +
			" WHERE request_id = :requestId AND a.active_flag = 'Y'", nativeQuery = true)
	List<Object[]> findGuestDetails(@Param("requestId") Integer requestId);

	@Query(value = "SELECT hostel_name, room_no " +
			" FROM "+ModelConstants.SCHEMA+".\"GUEST_ROOM_ALLOTMENT_INFO\" b " +
			" LEFT JOIN "+ModelConstants.SCHEMA+".\"HOSTEL_FLOOR_MASTER\" sf ON (sf.floor_id = b.building_id) " +
			" LEFT JOIN "+ModelConstants.SCHEMA+".\"HOSTEL_MASTER\" fm ON (fm.hostel_id = sf.hostel_id) " +
			" LEFT JOIN "+ModelConstants.SCHEMA+".\"HOSTEL_ROOM_INFO\" hri ON (b.roomid = hri.room_id)  " +
			" WHERE b.requestid = :requestId AND b.active_flag = 'Y'", nativeQuery = true)
	List<Object[]> findAllottedGuestRoomByRequestId(@Param("requestId") String requestId);

	@Query(value = "SELECT file_id,request_id,filename,description FROM "+ModelConstants.SCHEMA+".\"GUEST_FILES_INFORMATION\" " +
			" WHERE request_id = :requestId AND ACTIVE_FLAG = 'Y' AND SCHOOL_ID = 1", nativeQuery = true)
	List<Object[]> findFileInformation(@Param("requestId") Integer requestId);

	@Query(value = """
			select 
			    COUNT(CASE 
			              WHEN accommodation_type = 'Individual Guest Room' 
			                   AND warden_approval_status IN ('WardenApproveComplete') 
			              THEN 1 
			          END) AS warden_count_individual,
			    COUNT(CASE 
			              WHEN accommodation_type = 'Individual Guest Room' 
			                   AND warden_approval_status IN ('OverrideAndApproved') 
			              THEN 1 
			          END) AS ar_count_individual,
			    COUNT(CASE 
			              WHEN accommodation_type = 'Stay Along with Student' 
			                   AND warden_approval_status IN ('WardenApproveComplete') 
			              THEN 1 
			          END) AS warden_count_along_student,
				COUNT(CASE 
			              WHEN accommodation_type = 'Stay Along with Student' 
			                   AND warden_approval_status IN ('OverrideAndApproved') 
			              THEN 1 
			          END) AS ar_count_along_student
			FROM
			    schooldev."GUEST_ACCOMMODATION_REQUEST"
			WHERE
			    from_date=:fromDate
			""", nativeQuery = true)
	List<Object[]> getApprovalCountByStayType(@Param("fromDate") LocalDate fromDate);
}
