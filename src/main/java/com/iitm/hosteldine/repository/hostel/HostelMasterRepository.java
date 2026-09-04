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

	@Query(value = "SELECT " +
	               "hm.hostel_id, " +
	               "hm.hostel_name, " +
	               "hm.hostel_code, " +
	               "hm.hostel_gender_type, " +
	               "COALESCE(r_stats.total_capacity, 0) AS total_capacity, " +
	               "COALESCE(r_stats.total_utilized, 0) AS total_utilized, " +
	               "COALESCE(r_stats.single_rooms, 0) AS single_rooms, " +
	               "COALESCE(r_stats.single_cap, 0) AS single_cap, " +
	               "COALESCE(r_stats.single_occ, 0) AS single_occ, " +
	               "COALESCE(r_stats.double_rooms, 0) AS double_rooms, " +
	               "COALESCE(r_stats.double_cap, 0) AS double_cap, " +
	               "COALESCE(r_stats.double_occ, 0) AS double_occ, " +
	               "COALESCE(r_stats.triple_rooms, 0) AS triple_rooms, " +
	               "COALESCE(r_stats.triple_cap, 0) AS triple_cap, " +
	               "COALESCE(r_stats.triple_occ, 0) AS triple_occ, " +
	               "COALESCE(r_stats.quad_rooms, 0) AS quad_rooms, " +
	               "COALESCE(r_stats.quad_cap, 0) AS quad_cap, " +
	               "COALESCE(r_stats.quad_occ, 0) AS quad_occ, " +
	               "COALESCE(r_stats.dorm_rooms, 0) AS dorm_rooms, " +
	               "COALESCE(r_stats.dorm_cap, 0) AS dorm_cap, " +
	               "COALESCE(r_stats.dorm_occ, 0) AS dorm_occ, " +
	               "COALESCE(r_stats.pd_rooms, 0) AS pd_rooms, " +
	               "COALESCE(r_stats.pd_cap, 0) AS pd_cap, " +
	               "COALESCE(r_stats.pd_occ, 0) AS pd_occ, " +
	               "COALESCE(r_stats.guest_rooms, 0) AS guest_rooms, " +
	               "COALESCE(r_stats.guest_cap, 0) AS guest_cap, " +
	               "COALESCE(r_stats.guest_occ, 0) AS guest_occ " +
	               "FROM schooldev.dost_hostel_name hm " +
	               "LEFT JOIN ( " +
	               "    SELECT " +
	               "        hfm.hostel_id, " +
	               "        SUM(ri.capacity) AS total_capacity, " +
	               "        SUM(COALESCE(hra_cnt.occ_count, 0)) AS total_utilized, " +
	               "        SUM(CASE WHEN ri.capacity = 1 THEN 1 ELSE 0 END) AS single_rooms, " +
	               "        SUM(CASE WHEN ri.capacity = 1 THEN ri.capacity ELSE 0 END) AS single_cap, " +
	               "        SUM(CASE WHEN ri.capacity = 1 THEN COALESCE(hra_cnt.occ_count, 0) ELSE 0 END) AS single_occ, " +
	               "        SUM(CASE WHEN ri.capacity = 2 THEN 1 ELSE 0 END) AS double_rooms, " +
	               "        SUM(CASE WHEN ri.capacity = 2 THEN ri.capacity ELSE 0 END) AS double_cap, " +
	               "        SUM(CASE WHEN ri.capacity = 2 THEN COALESCE(hra_cnt.occ_count, 0) ELSE 0 END) AS double_occ, " +
	               "        SUM(CASE WHEN ri.capacity = 3 THEN 1 ELSE 0 END) AS triple_rooms, " +
	               "        SUM(CASE WHEN ri.capacity = 3 THEN ri.capacity ELSE 0 END) AS triple_cap, " +
	               "        SUM(CASE WHEN ri.capacity = 3 THEN COALESCE(hra_cnt.occ_count, 0) ELSE 0 END) AS triple_occ, " +
	               "        SUM(CASE WHEN ri.capacity = 4 THEN 1 ELSE 0 END) AS quad_rooms, " +
	               "        SUM(CASE WHEN ri.capacity = 4 THEN ri.capacity ELSE 0 END) AS quad_cap, " +
	               "        SUM(CASE WHEN ri.capacity = 4 THEN COALESCE(hra_cnt.occ_count, 0) ELSE 0 END) AS quad_occ, " +
	               "        SUM(CASE WHEN ri.capacity > 4 THEN 1 ELSE 0 END) AS dorm_rooms, " +
	               "        SUM(CASE WHEN ri.capacity > 4 THEN ri.capacity ELSE 0 END) AS dorm_cap, " +
	               "        SUM(CASE WHEN ri.capacity > 4 THEN COALESCE(hra_cnt.occ_count, 0) ELSE 0 END) AS dorm_occ, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'pd' THEN 1 ELSE 0 END) AS pd_rooms, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'pd' THEN ri.capacity ELSE 0 END) AS pd_cap, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'pd' THEN COALESCE(hra_cnt.occ_count, 0) ELSE 0 END) AS pd_occ, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) IN ('guest', 'official', 'icsr') THEN 1 ELSE 0 END) AS guest_rooms, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) IN ('guest', 'official', 'icsr') THEN ri.capacity ELSE 0 END) AS guest_cap, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) IN ('guest', 'official', 'icsr') THEN COALESCE(hra_cnt.occ_count, 0) ELSE 0 END) AS guest_occ " +
	               "    FROM schooldev.\"HOSTEL_FLOOR_MASTER\" hfm " +
	               "    JOIN schooldev.\"HOSTEL_ROOM_INFO\" ri ON (ri.building_id = hfm.floor_id AND ri.active_flag = 'Y') " +
	               "    LEFT JOIN ( " +
	               "        SELECT room_id, COUNT(room_allotment_id) AS occ_count " +
	               "        FROM schooldev.\"HOSTEL_ROOM_ALLOTMENT_INFO\" " +
	               "        WHERE active_flag = 'Y' AND vacate_date IS NULL AND shifted_date IS NULL " +
	               "        GROUP BY room_id " +
	               "    ) hra_cnt ON hra_cnt.room_id = ri.room_id " +
	               "    GROUP BY hfm.hostel_id " +
	               ") r_stats ON r_stats.hostel_id = hm.hostel_id " +
	               "WHERE hm.active_flag = 'Y' " +
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
}
