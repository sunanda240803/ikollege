package com.iitm.hosteldine.repository.hostel;

import com.iitm.hosteldine.model.hostel.HostelRoomInfoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HostelRoomInfoRepository extends JpaRepository<HostelRoomInfoEntity, Long> {
	List<HostelRoomInfoEntity> findAllByActiveFlagOrderByModifiedAtDesc(String statusActive);

	Optional<HostelRoomInfoEntity> findByIdAndActiveFlag(long hostelId, String statusActive);

	Optional<HostelRoomInfoEntity> findByActiveFlagAndBuildingIdAndBuildingHostelIdAndRoomNoAndIdNot(
			String statusActive, Long id, Long id2, String roomNo, Long id3);

	List<HostelRoomInfoEntity> findAllByActiveFlagAndBuildingIdAndBuildingHostelIdOrderByModifiedAtDesc(
			String statusActive, Long id, Long id2);

	List<HostelRoomInfoEntity> findAllByActiveFlagAndBuildingIdOrderByModifiedAtDesc(String statusActive, Long id);

	List<HostelRoomInfoEntity> findAllByActiveFlagOrderByRoomNo(String statusActive);
	
	boolean existsByActiveFlagAndBuildingId(String statusActive, long floorId);
	
	@Query(value = "SELECT DISTINCT room_no FROM schooldev.\"HOSTEL_ROOM_INFO\""
			+ " WHERE active_flag = :activeFlag ORDER BY room_no ASC", nativeQuery = true)
	List<String> findDistinctRoomNoByActiveFlag(String activeFlag);

	// Define the base query for room inventory details
	static final String ROOM_INVENTORY_DETAILS = "SELECT HR.* FROM schooldev.\"HOSTEL_ROOM_INFO\" HR"
	        + " JOIN schooldev.\"HOSTEL_FLOOR_MASTER\" HFM"
	        + " ON HR.building_id = HFM.floor_id AND HR.active_flag = HFM.active_flag"
	        + " JOIN schooldev.\"HOSTEL_MASTER\" HM"
	        + " ON HFM.hostel_id = HM.hostel_id AND HFM.active_flag = HM.active_flag"
	        + " WHERE HM.active_flag = :activeFlag AND HM.hostel_id = :hostelId";

	// Fetch all rooms by hostel ID
	@Query(value = ROOM_INVENTORY_DETAILS, nativeQuery = true)
	List<HostelRoomInfoEntity> findRoomByHostelId(@Param("activeFlag") String activeFlag, @Param("hostelId") Long hostelId);

	// Fetch a specific room by room number
	@Query(value = ROOM_INVENTORY_DETAILS + " AND HR.room_no = :roomNo", nativeQuery = true)
	HostelRoomInfoEntity findRoomByHostelIdAndRoomNo(@Param("activeFlag") String activeFlag, @Param("hostelId") Long hostelId, @Param("roomNo") String roomNo);

	@Query(value = ROOM_INVENTORY_DETAILS + " AND HR.room_no = :roomNo ORDER BY HR.created_at DESC LIMIT 1", nativeQuery = true)
	HostelRoomInfoEntity findRoomByHostelIdAndRoomNoRecentRecord(@Param("activeFlag") String activeFlag, @Param("hostelId") Long hostelId, @Param("roomNo") String roomNo);

	// Fetch a specific room by room ID
	@Query(value = ROOM_INVENTORY_DETAILS + " AND HR.room_id = :roomId", nativeQuery = true)
	HostelRoomInfoEntity findRoomByHostelIdAndRoomId(@Param("activeFlag") String activeFlag, @Param("hostelId") Long hostelId, @Param("roomId") Long roomId);

	Page<HostelRoomInfoEntity> findAllByActiveFlag(String statusActive, Pageable pageable);

	String hostelRoomOccupancySelect = "WITH date_series AS ("
			+ " SELECT generate_series(:startDate, :endDate, INTERVAL '1 day')::DATE AS stay_date"
			+ " ), room_student_count AS (SELECT"
			+ " hri.room_id, hri.room_no, hri.capacity, ds.stay_date, COUNT(vhraiv.student_type) AS student_count, hri.building_id"
			+ " FROM schooldev.\"HOSTEL_ROOM_INFO\" hri"
			+ " JOIN schooldev.\"HOSTEL_FLOOR_MASTER\" hfm ON hri.building_id = hfm.floor_id"
			+ " JOIN schooldev.\"HOSTEL_MASTER\" hm ON hm.hostel_id = hfm.hostel_id"
			+ " CROSS JOIN date_series ds"
			+ " LEFT JOIN schooldev.\"VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW\" vhraiv"
			+ " ON vhraiv.room_id = hri.room_id"
			+ " and vhraiv.active_flag = 'Y'"
			+ " and ds.stay_date between vhraiv.stay_from_date"
			+ " and coalesce(vhraiv.stay_to_date, vhraiv.shifted_date, vhraiv.vacate_date, ds.stay_date)"
			+ " WHERE hm.hostel_id = :hostelId and hri.active_flag = 'Y' and hri.official_guest_status not in ('guest', 'notfit')";

    String guestRoomOccupancySelect = "WITH date_series AS ("
            + " SELECT generate_series(:startDate, :endDate, INTERVAL '1 day')::DATE AS stay_date"
            + " ), room_student_count AS (SELECT"
            + " hri.room_id, hri.room_no, hri.capacity, ds.stay_date, count(graiv.sr_occupied) as student_count, hri.building_id"
            + " FROM schooldev.\"HOSTEL_ROOM_INFO\" hri"
            + " JOIN schooldev.\"HOSTEL_FLOOR_MASTER\" hfm ON hri.building_id = hfm.floor_id"
            + " JOIN schooldev.\"HOSTEL_MASTER\" hm ON hm.hostel_id = hfm.hostel_id"
            + " CROSS JOIN date_series ds"
            + " LEFT JOIN schooldev.\"GUEST_ROOM_ALLOTMENT_INFO_VIEW\" graiv"
            + " ON graiv.room_id = hri.room_id"
            + " and ds.stay_date between graiv.from_date and coalesce(graiv.to_date, ds.stay_date)"
            + " WHERE hm.hostel_id = :hostelId and hri.active_flag = 'Y' and hri.official_guest_status = 'guest'";

	String hostelRoomOccupancyGroup = " GROUP BY hri.room_id, hri.room_no, ds.stay_date)"
			+ " SELECT rsc.room_id, rsc.room_no, rsc.capacity, STRING_AGG(CAST(rsc.student_count AS TEXT), ',') AS student_counts"
			+ " , cast(avg(rsc.student_count) as integer), rsc.building_id"
			+ " FROM room_student_count rsc"
			+ " GROUP BY rsc.room_id, rsc.room_no, rsc.capacity, rsc.building_id"
			+ " order by cast(rsc.room_no as numeric), rsc.building_id";

	@Query(value = hostelRoomOccupancySelect + hostelRoomOccupancyGroup, nativeQuery = true)
	Optional<List<Object[]>> getHostelRoomOccupancy(Long hostelId, LocalDate startDate, LocalDate endDate);

    @Query(value = guestRoomOccupancySelect + hostelRoomOccupancyGroup, nativeQuery = true)
    Optional<List<Object[]>> getGuestRoomOccupancy(Long hostelId, LocalDate startDate, LocalDate endDate);

	@Query(value = hostelRoomOccupancySelect + " and hri.room_id = :roomId" + hostelRoomOccupancyGroup, nativeQuery = true)
	Optional<List<Object[]>> getHostelRoomOccupancy(Long hostelId, Long roomId, LocalDate startDate, LocalDate endDate);

	@Query(value = """
    SELECT sub_room_id, rai.stay_from_date, rai.stay_to_date, rai.vacate_date,
           rai.shifted_date, student_name, email, student_id, student_type, room_allotment_id , pwd_status
    FROM schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW" rai
    WHERE active_flag = :statusActive
    AND (
        CASE
            WHEN rai.shifted_date IS NOT NULL THEN :date BETWEEN rai.stay_from_date AND rai.shifted_date
            WHEN rai.vacate_date IS NOT NULL THEN :date BETWEEN rai.stay_from_date AND rai.vacate_date
            WHEN rai.stay_to_date IS NOT NULL THEN :date BETWEEN rai.stay_from_date AND rai.stay_to_date
            ELSE :date >= rai.stay_from_date
        END
    )
    AND room_id = :roomId
    order by sub_room_id
""", nativeQuery = true)
	Optional<List<Object[]>> getRoomOccupancyDetails(Long roomId, LocalDate date, String statusActive);

    @Query(value = """
        SELECT from_date, to_date, student_name, parent_email_id, student_id, room_allotment_id, guest_name, guest_relationship, no_of_days, no_of_persons
        FROM schooldev."GUEST_ROOM_ALLOTMENT_INFO_VIEW" graiv
        WHERE :date between from_date and to_date AND room_id = :roomId
    """, nativeQuery = true)
    Optional<List<Object[]>> getRoomOccupancyDetailsOfGuest(Long roomId, LocalDate date);

	@Query(value = """
    SELECT sub_room_id, rai.stay_from_date, rai.stay_to_date, rai.vacate_date, 
           rai.shifted_date, student_name, email, student_id, student_type, rai.room_allotment_id, pwd_status
    FROM schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW" rai
    WHERE active_flag = :statusActive
    AND (
        CASE
            WHEN rai.shifted_date IS NOT NULL THEN :date BETWEEN rai.stay_from_date AND rai.shifted_date
            WHEN rai.vacate_date IS NOT NULL THEN :date BETWEEN rai.stay_from_date AND rai.vacate_date
            WHEN rai.stay_to_date IS NOT NULL THEN :date BETWEEN rai.stay_from_date AND rai.stay_to_date
            ELSE :date >= rai.stay_from_date
        END
    )
    AND room_id = :roomId and sub_room_id = :subRoom
""", nativeQuery = true)
	Optional<List<Object[]>> getRoomOccupancyDetails(Long roomId, String subRoom, LocalDate date, String statusActive);

	@Query(value = """
        SELECT 
            a.request_id,
            first_name || ' ' || last_name AS student_name,
            date_of_birth,
            a.app_status,
            ca.stay_from,
            ca.stay_to,
            category,
            dining,
            b.email,
            a.stay_id,
            category_others          
        FROM schooldev."CANDIDATE_STAY_DATE_LIST_VIEW" a 
        LEFT JOIN schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO" vr 
            ON (vr.request_id::bigint = a.request_id) 
        JOIN schooldev."IIT_W_CANDIDATE_PERSONAL_DETAILS" b 
            ON (b.candidate_id = a.candidate_id) 
        JOIN schooldev."IIT_W_CANDIDATE_APPOINTMENT_REQUEST" ca 
            ON (ca.request_id = a.request_id) 
        WHERE UPPER(b.email) = UPPER(:email) 
            AND a.app_status IN ('Approved', 'Allotted', 'CheckedIn') 
            AND :givenDate BETWEEN a.stay_from AND a.stay_to order by ca.created_at limit 1
        """, nativeQuery = true)
	Optional<Object> findCandidateStayDetails(String email, LocalDate givenDate);

	@Query(value = """
        SELECT 
            a.request_id, 
            b.first_name || ' ' || b.last_name AS student_name, 
            b.dob AS date_of_birth, 
            a.status, 
            a.stay_from, 
            a.stay_to, 
            a.category, 
            a.dining,
            b.parent_email_id AS email,
            a.student_id
        FROM schooldev."IIT_W_STUDENT_APPOINTMENT_REQUEST" AS a
        JOIN schooldev."STUDENT_DETAILS_INFO" AS b ON (b.student_id = a.student_id)
        LEFT JOIN schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO" AS c ON (a.request_id = c.request_id::bigint) 
        WHERE a.student_id = :studentId 
        AND a.status IN ('Approved', 'Allotted', 'CheckedIn') 
        AND a.active_flag = 'Y' 
        AND :givenDate BETWEEN a.stay_from AND a.stay_to order by a.created_at limit 1
        """, nativeQuery = true)
	Optional<Object> findStudentStayDetails(String studentId, LocalDate givenDate);


	Page<HostelRoomInfoEntity> findAllByActiveFlagAndBuildingIdAndBuildingHostelId(String statusActive, Long floorId, Long hostelId,
			Pageable pageable);

	List<HostelRoomInfoEntity> findAllByActiveFlagAndBuildingHostelIdAndOfficialGuestStatusOrderByRoomNo
			(String statusActive, Long hostelId, String officialGuestStatus);

	@Query("SELECT e FROM HostelRoomInfoEntity e join HostelFloorMasterEntity hfm on (e.building.id=hfm.id and hfm.activeFlag = :statusActive) "
			+ "join HostelMasterEntity hm on(e.building.hostel.id = hm.id AND hm.activeFlag = :statusActive) "
			+ "WHERE e.activeFlag = :statusActive AND hfm.id= :floorId AND hm.id= :hostelId AND (" 
			+ "e.roomNo ILIKE CONCAT('%', :search, '%') OR "
			+ "e.officialGuestStatus ILIKE CONCAT('%', :search, '%'))")
	Page<HostelRoomInfoEntity> findByHostelRoomInfoSearchList(String statusActive, String search, long floorId,
			long hostelId, Pageable pageable);


	@Query(value = """
	select sdie.studentId,sdie.settlementFlag,srqv.approvalDate,srqv.hostelOrWardenApprovalStatus from StudentDetailsInfoEntity sdie
		left join StudentHostelRoomVacatingRequestViewEntity srqv on (srqv.studentId = sdie.studentId and srqv.activeFlag = :activeFlag
			and srqv.rejoiningDate is null and srqv.approvalDate is not null and srqv.hostelOrWardenApprovalStatus = 'Approved'
				and srqv.vacatingReason = 'Course Completed') where upper(sdie.studentId) = :studentId and sdie.activeFlag = :activeFlag
	""")
	Optional<List<Object[]>> getStudentHostelRoomVacatingDetails(String studentId,String activeFlag);

	@Query(value = """
	select hri.roomNo,hm.hostelName,hfm.floorName,hrai.subRoomId,hrai.stayFromDate,hm.hostelGenderType,hri.id from HostelRoomAllotmentInfoEntity hrai
		join HostelRoomInfoEntity hri on (hrai.roomId = hri.id and hri.activeFlag = :activeFlag)
			join HostelFloorMasterEntity hfm on (hfm.id = hri.building.id)
				join HostelMasterEntity hm on (hm.id = hfm.hostel.id)
					where hrai.studentId = :studentId and hrai.activeFlag = :activeFlag and hrai.vacateDate is null and
						hrai.shiftedDate is null
	""")
	Optional<List<Object[]>> getStudentRoomDetails(String studentId,String activeFlag);


	@Query(value = """
	 select aa.room_no,aa.occupied,aa.total_capacity, (total_capacity-occupied)as remaining_count,aa.occupied_seat
				from(select t1.room_no ,count(sr_occupied)as occupied,max(t1.capacity)as total_capacity,
		group_concat(t2.sr_occupied)as occupied_seat
		from schooldev."HOSTEL_ROOM_INFO" as t1
		join (Select a.room_no,d.sub_room_id as sr_occupied,a.capacity as capacity
			from schooldev."HOSTEL_ROOM_INFO" a
			join schooldev."HOSTEL_FLOOR_MASTER" b on(a.building_id=b.floor_id and a.official_guest_status  in ('0', 'PD'))
			left join schooldev."IIT_W_STUDENT_APPOINTMENT_REQUEST" c on (request_id= :requestId)
			left join  schooldev."COMPLETE_HOSTEL_ALLOTMENT_VIEW" as d  on (b.floor_id = d.floor_id and a.room_id = d.room_id
				and (case when (vacation_category='N' or allocation_type='V')
				then (c.stay_from::date-1,  c.stay_to::date+1) overlaps (stay_from_date::date ,  coalesce(stay_to_date::date, shifted_date::date,'2089-10-10')) end)
				and d.active_flag= :activeFlag)
				where b.hostel_id= :hostelId and a.active_flag = :activeFlag and (:roomNo is null or a.room_no = :roomNo)
				group by a.room_no,d.sub_room_id,a.capacity,a.vac_capacity
				order by a.room_no) as t2 on (t2.room_no=t1.room_no)
		join schooldev."HOSTEL_FLOOR_MASTER" t3 on (t1.building_id=t3.floor_id)
		where  t1.active_flag = :activeFlag and t3.active_flag = :activeFlag and t3.hostel_id= :hostelId
		group by t1.room_no  order by t1.room_no)as aa
    """, nativeQuery = true)
	Optional<List<Object[]>> getAvailableRooms(Long hostelId, Long requestId, String roomNo, String activeFlag);

	@Query(value = """
		select sar.stay_from,sar.stay_to, sar.category, sar.dining, sar.student_id, sdv.student_name, sdv.dob, sdv.gender, sdv.student_iitm_smail, hr.room_id, hr.building_id  from schooldev."IIT_W_STUDENT_APPOINTMENT_REQUEST" sar
               join schooldev."ALL_STUDENTS_DETAILS_VIEW" sdv on(sdv.student_id = sar.student_id)
               join schooldev."HOSTEL_ROOM_INFO" hr on(room_no = :roomNo and hr.active_flag = :activeFlag)
               join schooldev."HOSTEL_FLOOR_MASTER" hf on (hf.floor_id = hr.building_id and hf.active_flag = :activeFlag)
               where  hf.hostel_id = :hostelId and hr.room_no = :roomNo and sar.request_id = :requestId and sar.status = :status and sar.active_flag = :activeFlag limit 1
    """, nativeQuery = true)
	Optional<List<Object[]>> getAllotStudent(Long hostelId, String roomNo, Long requestId, String status, String activeFlag);

	@Query(value = """
	 select aa.room_no,aa.occupied,aa.total_capacity, (total_capacity-occupied)as remaining_count,aa.occupied_seat
				from(select t1.room_no ,count(sr_occupied)as occupied,max(t1.capacity)as total_capacity,
		group_concat(t2.sr_occupied)as occupied_seat
		from schooldev."HOSTEL_ROOM_INFO" as t1
		join (Select a.room_no,d.sub_room_id as sr_occupied,a.capacity as capacity
			from schooldev."HOSTEL_ROOM_INFO" a
			join schooldev."HOSTEL_FLOOR_MASTER" b on(a.building_id=b.floor_id and a.official_guest_status  in ('0', 'PD'))
			left join schooldev."CANDIDATE_STAY_DATE_LIST_VIEW" c on (stay_id = :stayId and request_id= :requestId)
			left join  schooldev."COMPLETE_HOSTEL_ALLOTMENT_VIEW" as d  on (b.floor_id = d.floor_id and a.room_id = d.room_id
				and (case when (vacation_category='N' or allocation_type='V')
				then (c.stay_from::date-1,  c.stay_to::date+1) overlaps (stay_from_date::date ,  coalesce(stay_to_date::date, shifted_date::date,'2089-10-10')) end)
				and d.active_flag= :activeFlag)
				where b.hostel_id= :hostelId and a.active_flag = :activeFlag and (:roomNo is null or a.room_no = :roomNo)
				group by a.room_no,d.sub_room_id,a.capacity,a.vac_capacity
				order by a.room_no) as t2 on (t2.room_no=t1.room_no)
		join schooldev."HOSTEL_FLOOR_MASTER" t3 on (t1.building_id=t3.floor_id)
		where  t1.active_flag = :activeFlag and t3.active_flag = :activeFlag and t3.hostel_id= :hostelId
		group by t1.room_no  order by t1.room_no)as aa
    """, nativeQuery = true)
	Optional<List<Object[]>> getAvailableRoomsForStay(Long hostelId, Long requestId,Long stayId, String roomNo, String activeFlag);

	@Query(value = """
			SELECT hostel_id, hostel_name, SUM(total) AS total_no_of_seats, SUM(occupied) AS occupied,
			       SUM(vacancy) AS vacancy,
			       SUM(CASE WHEN vacancy < 0 THEN 0 ELSE vacancy END) AS exact_vacancy
			FROM (
			    SELECT hm.hostel_id, hm.hostel_name, hri.room_no, MAX(hri.capacity) AS total,
			           SUM(CASE WHEN sub_room_id IS NULL THEN 0 ELSE 1 END) AS occupied,
			           (MAX(hri.capacity) - SUM(CASE WHEN sub_room_id IS NULL THEN 0 ELSE 1 END)) AS vacancy
			    FROM  schooldev."HOSTEL_ROOM_INFO" hri
			    JOIN schooldev."HOSTEL_FLOOR_MASTER" hfm ON (hri.building_id = hfm.floor_id AND hfm.active_flag = 'Y')
			    JOIN schooldev."HOSTEL_MASTER" hm ON (hfm.hostel_id = hm.hostel_id AND hm.active_flag = 'Y')
			    LEFT JOIN schooldev."COMPLETE_HOSTEL_ALLOTMENT_VIEW" v
			           ON (v.room_id = hri.room_id AND stay_from_date <= CURRENT_DATE
			           AND (COALESCE(stay_to_date, shifted_date)::date >= CURRENT_DATE
			                OR (stay_to_date IS NULL AND shifted_date IS NULL))
			           AND v.active_flag = 'Y')
			    WHERE hri.active_flag = 'Y' AND official_guest_status = '0'
			    GROUP BY hm.hostel_id, hri.room_no
			) AS aa
			WHERE CASE WHEN '0'::bigint = 0 THEN hostel_id != 0 ELSE hostel_id = '0'::bigint END
			GROUP BY hostel_id, hostel_name
			ORDER BY hostel_name
			""", countQuery = """
			SELECT COUNT(*) FROM (
			    SELECT hostel_id
			    FROM (
			        SELECT hm.hostel_id, hri.room_no
			        FROM  schooldev."HOSTEL_ROOM_INFO" hri
			        JOIN schooldev."HOSTEL_FLOOR_MASTER" hfm ON (hri.building_id = hfm.floor_id AND hfm.active_flag = 'Y')
			        JOIN schooldev."HOSTEL_MASTER" hm ON (hfm.hostel_id = hm.hostel_id AND hm.active_flag = 'Y')
			        LEFT JOIN schooldev."COMPLETE_HOSTEL_ALLOTMENT_VIEW" v
			               ON (v.room_id = hri.room_id AND stay_from_date <= CURRENT_DATE
			               AND (COALESCE(stay_to_date, shifted_date)::date >= CURRENT_DATE
			                    OR (stay_to_date IS NULL AND shifted_date IS NULL))
			               AND v.active_flag = 'Y')
			        WHERE hri.active_flag = 'Y' AND official_guest_status = '0'
			        GROUP BY hm.hostel_id, hri.room_no
			    ) AS grouped_rooms
			    GROUP BY hostel_id
			) AS grouped_hostels
			""", nativeQuery = true)
	Page<Object[]> getCurrentHostelVacancyStatusList(Pageable pageable);
	
	@Query(value = """
			SELECT SPLIT_PART(hri.room_no, '-', 1) AS room_no,SPLIT_PART(hri.room_no, '-', 2) AS ROOMNAME,hfm.FLOOR_NAME, hri.capacity,
			COUNT (chav.room_id) AS ALLOTED, (hri.capacity-COUNT (chav.room_id)) AS VACANCY FROM schooldev."HOSTEL_ROOM_INFO" hri
			LEFT JOIN schooldev."COMPLETE_HOSTEL_ALLOTMENT_VIEW" chav
			ON (chav.active_flag='Y' and CURRENT_DATE between stay_from_date and coalesce(shifted_date, vacate_date, stay_to_date, CURRENT_DATE)
			AND hri.room_id = chav.room_id)
			join schooldev."HOSTEL_FLOOR_MASTER" hfm on (hfm.floor_id=hri.building_id) where hri.building_id IN
			(SELECT hfm1.floor_id FROM schooldev."HOSTEL_FLOOR_MASTER" hfm1 WHERE hfm1.hostel_id=:hostelId)
			and hri.active_flag='Y' and (:floorId = 0 OR hfm.floor_id = :floorId)
			GROUP BY hri.room_no, hfm.FLOOR_NAME,hri.capacity HAVING COUNT (chav.room_id) < hri.capacity
			ORDER BY CASE WHEN hri.room_no  = '' THEN NULL ELSE TO_NUMBER(hri.room_no,'99999') end
			   """, nativeQuery = true)
	Page<Object[]> getHostelVacantRoomDetails(Long hostelId, long floorId, Pageable pageable);

	@Query(value = """
						SELECT SPLIT_PART(hri.room_no, '-', 1) AS room_no,SPLIT_PART(hri.room_no, '-', 2) AS ROOMNAME,FLOOR_NAME FROM
			schooldev."HOSTEL_ROOM_INFO" hri LEFT join schooldev."HOSTEL_ROOM_ALLOTMENT_INFO" hrai ON
			(hrai.active_flag='Y' and hrai.vacate_date is null and hrai.shifted_date is null AND hri.school_id = hrai.school_id AND hri.room_id = hrai.room_id)
			join schooldev."HOSTEL_FLOOR_MASTER" hfm on (floor_id=hri.building_id and hfm.school_id = hri.school_id) where hri.building_id IN
			(SELECT hfm1.floor_id FROM schooldev."HOSTEL_FLOOR_MASTER" hfm1 WHERE hfm1.hostel_id= :hostelId and hfm.school_id = hfm1.school_id) and hri.active_flag='Y'
			and hri.school_id =1 and (:floorId = 0 OR hfm.floor_id = :floorId) and hri.capacity = 0 ORDER BY CASE WHEN hri.room_no  = '' THEN NULL ELSE TO_NUMBER(hri.room_no,'99999') end;
						   """, nativeQuery = true)
	Page<Object[]> getHostelVacantRoomDetailsCommon(Long hostelId, long floorId, Pageable pageable);


	@Query(value = "SELECT hm.hostel_id, hm.hostel_name, a.room_id, hri.room_no, a.sub_room_id, " +
			"a.allocation_type, a.student_id, a.student_name, a.email, a.nature_of_appointment, a.vacate_date " +
			"FROM schooldev.\"VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW\" a " +
			"INNER JOIN schooldev.\"HOSTEL_FLOOR_MASTER\" hfm ON hfm.floor_id = a.building_id " +
			"INNER JOIN schooldev.\"HOSTEL_MASTER\" hm ON (hfm.hostel_id = hm.hostel_id) " +
			"INNER JOIN schooldev.\"HOSTEL_ROOM_INFO\" hri ON (hri.room_id = a.room_id) " +
			"WHERE a.stay_from_date <= CURRENT_DATE " +
			"AND COALESCE(a.shifted_date, a.vacate_date, a.stay_to_date, CURRENT_DATE) >= CURRENT_DATE " +
			"AND a.active_flag = 'Y' " +
			"AND (:hostelId = -1 OR hm.hostel_id = :hostelId)", nativeQuery = true)
	List<Object[]> findCurrentAllocationsByHostelId(Long hostelId);


	@Query(value = """
			select  hm.hostel_id ,hm.hostel_name ,hri.room_id, hri.room_no
			from schooldev."HOSTEL_FLOOR_MASTER" hfm\s
			inner join  schooldev."HOSTEL_MASTER" hm on (hfm.hostel_id = hm.hostel_id)\s
			inner join  schooldev."HOSTEL_ROOM_INFO" hri on (hri.building_id = hfm.floor_id)\s
			LEFT JOIN
			    schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW" va ON (hri.room_id = va.room_id)
			WHERE\s
			    (:hostelId = -1 OR hm.hostel_id = :hostelId)
			    AND va.room_id IS NULL;
			""", nativeQuery = true)
	List<Object[]> findVacantRoomByHostelId(Long hostelId);


	@Query(value = """
        SELECT 
            aa.room_no AS room_no,
            aa.room_id,
            aa.total_capacity AS total_capacity,
            aa.remainingcount AS remaining_count,
            aa.floor_id
        FROM (
            SELECT 
                t1.floor_name,t1.floor_id,
                t2.room_no,t2.room_id,
                COUNT(t1.sr_occupied) AS occupied,
                MAX(t1.capacity) AS total_capacity,
                (CASE WHEN COUNT(t1.sr_occupied) = 1 THEN 0 ELSE 1 END) AS remainingcount
            FROM schooldev."HOSTEL_ROOM_INFO" AS t2
            JOIN (
                SELECT 
                    hfm.floor_name,hfm.floor_id,
                    hri.room_no,hri.room_id,
                    grai.occupied_status AS sr_occupied,
                    hri.capacity AS capacity
                FROM schooldev."HOSTEL_ROOM_INFO" hri
                JOIN schooldev."HOSTEL_FLOOR_MASTER" hfm 
                    ON hri.building_id = hfm.floor_id
                LEFT JOIN schooldev."GUEST_ACCOMMODATION_REQUEST" gar 
                    ON (gar.id = :requestId AND gar.accommodation_type = 'Individual Guest Room')
                LEFT JOIN schooldev."GUEST_ROOM_ALLOTMENT_INFO" grai 
                    ON (
                        hri.building_id = grai.building_id 
                        AND hri.room_id = grai.roomid
                        and (stay_from_date - interval '1 day', stay_to_date + interval '1 day') 
                        overlaps (cast(:fromDate as date), cast(:toDate as date))
                        AND grai.active_flag = 'Y'
                    )
                WHERE 
                    hfm.hostel_id = :hostelId
                    AND hri.active_flag = 'Y'
                    AND hfm.active_flag = 'Y'
                    AND hri.official_guest_status = 'guest'
                GROUP BY hri.room_no,hri.room_id, hri.capacity, hfm.floor_name,hfm.floor_id, grai.occupied_status
            ) AS t1 
                ON t1.room_no = t2.room_no
            JOIN schooldev."HOSTEL_FLOOR_MASTER" fm 
                ON t2.building_id = fm.floor_id
            WHERE 
                t2.active_flag = 'Y'
                AND fm.active_flag = 'Y'
                AND fm.hostel_id = :hostelId
                AND t2.official_guest_status = 'guest'
            GROUP BY t2.room_no,t2.room_id, t1.floor_name,t1.floor_id 
            ORDER BY t2.room_no
        ) AS aa
        """, nativeQuery = true)
	List<Object[]> findAvailableGuestRoomsByHostel(
			@Param("requestId") Long requestId,
			@Param("hostelId") Long hostelId,
			@Param("fromDate") String fromDate,
			@Param("toDate") String toDate
	);
}