package com.iitm.hosteldine.repository.hostel;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iitm.hosteldine.model.hostel.HostelMasterEntity;

public interface HostelMasterRepository extends JpaRepository<HostelMasterEntity, Long> {

	List<HostelMasterEntity> findAllByActiveFlagOrderByHostelName(String statusActive);

	Optional<HostelMasterEntity> findByIdAndActiveFlag(long hostelId, String statusActive);

	List<HostelMasterEntity> findByActiveFlagAndHostelNameIgnoreCaseAndIdNot(String statusActive, String hostelName,
			Long id);

	List<HostelMasterEntity> findByActiveFlagAndHostelShortCodeIgnoreCaseAndIdNot(String statusActive,
			String hostelCode, long id);

	Page<HostelMasterEntity> findAllByActiveFlag(String statusActive, Pageable pageable);

	@Query("SELECT hme FROM HostelMasterEntity hme WHERE hme.activeFlag = :statusActive AND (" +
	           "hme.hostelName ILIKE CONCAT('%', :search, '%') OR " +
	           "hme.hostelOfficeEmail ILIKE CONCAT('%', :search, '%') OR " +
	           "hme.hostelShortCode ILIKE CONCAT('%', :search, '%'))") 
	Page<HostelMasterEntity> findByHostelMasterSearchList(String statusActive, Pageable pageable, String search);

    @Query(value = "SELECT h FROM HostelMasterEntity h"
            + " WHERE UPPER(h.hostelName) = UPPER(:hostelName) AND h.activeFlag = :activeFlag")
    HostelMasterEntity checkHostelExist(String hostelName, String activeFlag);

    @Query(value = "SELECT a, b, c FROM HostelMasterEntity a"
    		+ " LEFT JOIN HostelFloorMasterEntity b ON (a.id = b.hostel.id AND b.activeFlag = :activeFlag)"
    		+ " LEFT JOIN HostelRoomInfoEntity c ON (b.id = c.building.id AND c.activeFlag = :activeFlag)"
    		+ " WHERE UPPER(a.hostelName) = UPPER(:hostelName) AND c.roomNo = :roomNo AND a.activeFlag = :activeFlag")
    Object[] getHostelAndFloorAndRoomDetails(String hostelName, String roomNo, String activeFlag);

	List<HostelMasterEntity> findByActiveFlagAndHostelName(String statusActive, String hostelName);

	@Query(value = "WITH room_base AS ( " +
	               "    SELECT " +
	               "        hfm.hostel_id, " +
	               "        ri.capacity AS cap, " +
	               "        COALESCE(hra_cnt.occ_count, 0) AS occ, " +
	               "        CASE " +
	               "            WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'pd' THEN 'pd' " +
	               "            WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'guest' THEN 'guest' " +
	               "            WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'notfit' THEN 'notfit' " +
	               "            ELSE 'std' " +
	               "        END AS cat " +
	               "    FROM schooldev.\"HOSTEL_FLOOR_MASTER\" hfm " +
	               "    JOIN schooldev.\"HOSTEL_ROOM_INFO\" ri ON (ri.building_id = hfm.floor_id AND ri.active_flag = 'Y') " +
	               "    LEFT JOIN ( " +
	               "        SELECT room_id, COUNT(room_allotment_id) AS occ_count " +
	               "        FROM schooldev.\"HOSTEL_ROOM_ALLOTMENT_INFO\" " +
	               "        WHERE active_flag = 'Y' AND vacate_date IS NULL AND shifted_date IS NULL " +
	               "        GROUP BY room_id " +
	               "    ) hra_cnt ON hra_cnt.room_id = ri.room_id " +
	               ") " +
	               "SELECT " +
	               "    hm.hostel_id, " +
	               "    hm.hostel_name, " +
	               "    hm.hostel_code, " +
	               "    hm.hostel_gender_type, " +
	               "    COALESCE(SUM(rb.cap), 0) AS total_capacity, " +
	               "    COALESCE(SUM(rb.occ), 0) AS total_utilized, " +
	               "    COALESCE(COUNT(rb.cap), 0) AS total_rooms, " +
	               "    COALESCE(SUM(CASE WHEN rb.occ >= rb.cap THEN 1 ELSE 0 END), 0) AS fully_occupied_rooms, " +
	               "    COALESCE(SUM(CASE WHEN rb.occ > 0 AND rb.occ < rb.cap THEN 1 ELSE 0 END), 0) AS partial_occupied_rooms, " +
	               "    COALESCE(SUM(CASE WHEN rb.occ = 0 THEN 1 ELSE 0 END), 0) AS fully_vacant_rooms, " +
	               "    COALESCE(SUM(CASE WHEN rb.occ > 0 AND rb.occ < rb.cap THEN rb.occ ELSE 0 END), 0) AS partial_occupied_beds, " +
	               "    COALESCE(SUM(CASE WHEN rb.occ > 0 AND rb.occ < rb.cap THEN (rb.cap - rb.occ) ELSE 0 END), 0) AS partial_vacant_beds, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap = 1 AND rb.cat = 'std' THEN 1 ELSE 0 END), 0) AS single_rooms, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap = 1 AND rb.cat = 'std' THEN rb.cap ELSE 0 END), 0) AS single_cap, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap = 1 AND rb.cat = 'std' THEN rb.occ ELSE 0 END), 0) AS single_occ, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap = 2 AND rb.cat = 'std' THEN 1 ELSE 0 END), 0) AS double_rooms, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap = 2 AND rb.cat = 'std' THEN rb.cap ELSE 0 END), 0) AS double_cap, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap = 2 AND rb.cat = 'std' THEN rb.occ ELSE 0 END), 0) AS double_occ, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap = 3 AND rb.cat = 'std' THEN 1 ELSE 0 END), 0) AS triple_rooms, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap = 3 AND rb.cat = 'std' THEN rb.cap ELSE 0 END), 0) AS triple_cap, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap = 3 AND rb.cat = 'std' THEN rb.occ ELSE 0 END), 0) AS triple_occ, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap = 4 AND rb.cat = 'std' THEN 1 ELSE 0 END), 0) AS quad_rooms, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap = 4 AND rb.cat = 'std' THEN rb.cap ELSE 0 END), 0) AS quad_cap, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap = 4 AND rb.cat = 'std' THEN rb.occ ELSE 0 END), 0) AS quad_occ, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap > 4 AND rb.cat = 'std' THEN 1 ELSE 0 END), 0) AS dorm_rooms, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap > 4 AND rb.cat = 'std' THEN rb.cap ELSE 0 END), 0) AS dorm_cap, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap > 4 AND rb.cat = 'std' THEN rb.occ ELSE 0 END), 0) AS dorm_occ, " +
	               "    COALESCE(SUM(CASE WHEN rb.cat = 'pd' THEN 1 ELSE 0 END), 0) AS pd_rooms, " +
	               "    COALESCE(SUM(CASE WHEN rb.cat = 'pd' THEN rb.cap ELSE 0 END), 0) AS pd_cap, " +
	               "    COALESCE(SUM(CASE WHEN rb.cat = 'pd' THEN rb.occ ELSE 0 END), 0) AS pd_occ, " +
	               "    COALESCE(SUM(CASE WHEN rb.cat = 'guest' THEN 1 ELSE 0 END), 0) AS guest_rooms, " +
	               "    COALESCE(SUM(CASE WHEN rb.cat = 'guest' THEN rb.cap ELSE 0 END), 0) AS guest_cap, " +
	               "    COALESCE(SUM(CASE WHEN rb.cat = 'guest' THEN rb.occ ELSE 0 END), 0) AS guest_occ, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap = 1 AND rb.cat = 'std' AND rb.occ = 0 THEN 1 ELSE 0 END), 0) AS single_full_vac, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap = 2 AND rb.cat = 'std' AND rb.occ = 0 THEN 1 ELSE 0 END), 0) AS double_full_vac, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap = 2 AND rb.cat = 'std' AND rb.occ > 0 AND rb.occ < 2 THEN rb.occ ELSE 0 END), 0) AS double_part_occ_beds, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap = 2 AND rb.cat = 'std' AND rb.occ > 0 AND rb.occ < 2 THEN (2 - rb.occ) ELSE 0 END), 0) AS double_part_vac_beds, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap = 3 AND rb.cat = 'std' AND rb.occ = 0 THEN 1 ELSE 0 END), 0) AS triple_full_vac, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap = 3 AND rb.cat = 'std' AND rb.occ > 0 AND rb.occ < 3 THEN rb.occ ELSE 0 END), 0) AS triple_part_occ_beds, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap = 3 AND rb.cat = 'std' AND rb.occ > 0 AND rb.occ < 3 THEN (3 - rb.occ) ELSE 0 END), 0) AS triple_part_vac_beds, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap = 4 AND rb.cat = 'std' AND rb.occ = 0 THEN 1 ELSE 0 END), 0) AS quad_full_vac, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap = 4 AND rb.cat = 'std' AND rb.occ > 0 AND rb.occ < 4 THEN rb.occ ELSE 0 END), 0) AS quad_part_occ_beds, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap = 4 AND rb.cat = 'std' AND rb.occ > 0 AND rb.occ < 4 THEN (4 - rb.occ) ELSE 0 END), 0) AS quad_part_vac_beds, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap > 4 AND rb.cat = 'std' AND rb.occ = 0 THEN 1 ELSE 0 END), 0) AS dorm_full_vac, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap > 4 AND rb.cat = 'std' AND rb.occ > 0 AND rb.occ < rb.cap THEN rb.occ ELSE 0 END), 0) AS dorm_part_occ_beds, " +
	               "    COALESCE(SUM(CASE WHEN rb.cap > 4 AND rb.cat = 'std' AND rb.occ > 0 AND rb.occ < rb.cap THEN (rb.cap - rb.occ) ELSE 0 END), 0) AS dorm_part_vac_beds, " +
	               "    COALESCE(SUM(CASE WHEN rb.cat = 'pd' AND rb.occ = 0 THEN 1 ELSE 0 END), 0) AS pd_full_vac, " +
	               "    COALESCE(SUM(CASE WHEN rb.cat = 'pd' AND rb.occ > 0 AND rb.occ < rb.cap THEN rb.occ ELSE 0 END), 0) AS pd_part_occ_beds, " +
	               "    COALESCE(SUM(CASE WHEN rb.cat = 'pd' AND rb.occ > 0 AND rb.occ < rb.cap THEN (rb.cap - rb.occ) ELSE 0 END), 0) AS pd_part_vac_beds, " +
	               "    COALESCE(SUM(CASE WHEN rb.cat = 'guest' AND rb.occ = 0 THEN 1 ELSE 0 END), 0) AS guest_full_vac, " +
	               "    COALESCE(SUM(CASE WHEN rb.cat = 'guest' AND rb.occ > 0 AND rb.occ < rb.cap THEN rb.occ ELSE 0 END), 0) AS guest_part_occ_beds, " +
	               "    COALESCE(SUM(CASE WHEN rb.cat = 'guest' AND rb.occ > 0 AND rb.occ < rb.cap THEN (rb.cap - rb.occ) ELSE 0 END), 0) AS guest_part_vac_beds " +
	               "FROM schooldev.dost_hostel_name hm " +
	               "LEFT JOIN room_base rb ON rb.hostel_id = hm.hostel_id " +
	               "WHERE hm.active_flag = 'Y' " +
	               "GROUP BY hm.hostel_id, hm.hostel_name, hm.hostel_code, hm.hostel_gender_type " +
	               "ORDER BY hm.hostel_name", nativeQuery = true)
	List<Object[]> getHostelCapacityListByYear(@Param("startYear") Integer startYear);

	@Query(value = "SELECT " +
	               "hm.hostel_id, " +
	               "hm.hostel_name, " +
	               "UPPER(SUBSTRING(hra.student_id FROM 1 FOR 2)) AS course_code, " +
	               "COALESCE(dept.dept_name, UPPER(SUBSTRING(hra.student_id FROM 1 FOR 2))) AS course_name, " +
	               "CASE WHEN SUBSTRING(hra.student_id FROM 3 FOR 2) ~ '^[0-9]{2}$' THEN '20' || SUBSTRING(hra.student_id FROM 3 FOR 2) ELSE 'Other' END AS batch_year, " +
	               "COUNT(hra.room_allotment_id) AS student_count " +
	               "FROM schooldev.dost_hostel_name hm " +
	               "JOIN schooldev.\"HOSTEL_FLOOR_MASTER\" hfm ON hfm.hostel_id = hm.hostel_id " +
	               "JOIN schooldev.\"HOSTEL_ROOM_INFO\" ri ON (ri.building_id = hfm.floor_id AND ri.active_flag = 'Y') " +
	               "JOIN schooldev.\"HOSTEL_ROOM_ALLOTMENT_INFO\" hra ON (hra.room_id = ri.room_id AND hra.active_flag = 'Y' AND hra.vacate_date IS NULL AND hra.shifted_date IS NULL) " +
	               "LEFT JOIN schooldev.dost_election_department dept ON (dept.dept_code = UPPER(SUBSTRING(hra.student_id FROM 1 FOR 2)) OR dept.alt_dept_code = UPPER(SUBSTRING(hra.student_id FROM 1 FOR 2)) OR dept.alt_dept_code2 = UPPER(SUBSTRING(hra.student_id FROM 1 FOR 2))) " +
	               "WHERE hm.active_flag = 'Y' AND (:hostelId = 0 OR hm.hostel_id = :hostelId) " +
	               "GROUP BY hm.hostel_id, hm.hostel_name, course_code, course_name, batch_year " +
	               "ORDER BY hm.hostel_name, course_name, batch_year DESC", nativeQuery = true)
	List<Object[]> getHostelStudentDistributionByYear(@Param("hostelId") Long hostelId, @Param("startYear") Integer startYear);

	@Query(value = "SELECT " +
	               "hm.hostel_id, " +
	               "hm.hostel_name, " +
	               "hm.hostel_code, " +
	               "COALESCE(g_rooms.total_guest_rooms, 0) AS total_guest_rooms, " +
	               "COALESCE(g_rooms.total_guest_cap, 0) AS total_guest_cap, " +
	               "COALESCE(g_occ.today_guest_utilized, 0) AS today_guest_utilized, " +
	               "GREATEST(0, COALESCE(g_rooms.total_guest_rooms, 0) - COALESCE(g_occ.today_guest_utilized, 0)) AS today_guest_vacant, " +
	               "COALESCE(chg.individual_room_amount, 800) AS single_room_tariff, " +
	               "COALESCE(chg.individual_room_multiple_amount, 1200) AS shared_room_tariff, " +
	               "COALESCE(chg.amount, 250) AS lodging_base_charge, " +
	               "COALESCE(chg.description, 'Standard Tariff') AS tariff_description, " +
	               "COALESCE(g_rooms.single_guest_rooms, 0) AS single_guest_rooms, " +
	               "GREATEST(0, COALESCE(g_rooms.single_guest_rooms, 0) - COALESCE(g_occ.today_single_utilized, 0)) AS single_guest_vacant, " +
	               "COALESCE(g_rooms.shared_guest_rooms, 0) AS shared_guest_rooms, " +
	               "GREATEST(0, COALESCE(g_rooms.shared_guest_rooms, 0) - COALESCE(g_occ.today_shared_utilized, 0)) AS shared_guest_vacant " +
	               "FROM schooldev.dost_hostel_name hm " +
	               "LEFT JOIN ( " +
	               "    SELECT " +
	               "        hfm.hostel_id, " +
	               "        COUNT(ri.room_id) AS total_guest_rooms, " +
	               "        SUM(ri.capacity) AS total_guest_cap, " +
	               "        SUM(CASE WHEN ri.capacity = 1 THEN 1 ELSE 0 END) AS single_guest_rooms, " +
	               "        SUM(CASE WHEN ri.capacity > 1 THEN 1 ELSE 0 END) AS shared_guest_rooms " +
	               "    FROM schooldev.\"HOSTEL_FLOOR_MASTER\" hfm " +
	               "    JOIN schooldev.\"HOSTEL_ROOM_INFO\" ri ON (ri.building_id = hfm.floor_id AND ri.active_flag = 'Y') " +
	               "    WHERE LOWER(COALESCE(ri.official_guest_status, '')) = 'guest' " +
	               "    GROUP BY hfm.hostel_id " +
	               ") g_rooms ON g_rooms.hostel_id = hm.hostel_id " +
	               "LEFT JOIN ( " +
	               "    SELECT " +
	               "        hfm.hostel_id, " +
	               "        COUNT(DISTINCT ri.room_id) AS today_guest_utilized, " +
	               "        COUNT(DISTINCT CASE WHEN ri.capacity = 1 THEN ri.room_id END) AS today_single_utilized, " +
	               "        COUNT(DISTINCT CASE WHEN ri.capacity > 1 THEN ri.room_id END) AS today_shared_utilized " +
	               "    FROM schooldev.\"HOSTEL_ROOM_ALLOTMENT_INFO\" hra " +
	               "    JOIN schooldev.\"HOSTEL_ROOM_INFO\" ri ON (ri.room_id = hra.room_id AND ri.active_flag = 'Y') " +
	               "    JOIN schooldev.\"HOSTEL_FLOOR_MASTER\" hfm ON hfm.floor_id = ri.building_id " +
	               "    WHERE hra.active_flag = 'Y' AND hra.vacate_date IS NULL AND hra.shifted_date IS NULL " +
	               "      AND LOWER(COALESCE(ri.official_guest_status, '')) = 'guest' " +
	               "    GROUP BY hfm.hostel_id " +
	               ") g_occ ON g_occ.hostel_id = hm.hostel_id " +
	               "LEFT JOIN ( " +
	               "    SELECT * FROM schooldev.\"GUEST_ACCOMMODATION_CHARGES\" " +
	               "    WHERE active_flag = 'Y' " +
	               "    ORDER BY id DESC LIMIT 1 " +
	               ") chg ON 1=1 " +
	               "WHERE hm.active_flag = 'Y' AND (:hostelId = 0 OR hm.hostel_id = :hostelId) " +
	               "ORDER BY hm.hostel_name", nativeQuery = true)
	List<Object[]> getGuestRoomTariffReport(@Param("hostelId") Long hostelId);
}
