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
	               "COALESCE(r_stats.single_vac_rooms, 0) AS single_vac_rooms, " +
	               "COALESCE(r_stats.single_occ_rooms, 0) AS single_occ_rooms, " +
	               "COALESCE(r_stats.single_part_vac_rooms, 0) AS single_part_vac_rooms, " +
	               "COALESCE(r_stats.single_part_vac_beds, 0) AS single_part_vac_beds, " +
	               "COALESCE(r_stats.single_over_rooms, 0) AS single_over_rooms, " +
	               "COALESCE(r_stats.single_over_seats, 0) AS single_over_seats, " +
	               "COALESCE(r_stats.double_rooms, 0) AS double_rooms, " +
	               "COALESCE(r_stats.double_cap, 0) AS double_cap, " +
	               "COALESCE(r_stats.double_occ, 0) AS double_occ, " +
	               "COALESCE(r_stats.double_vac_rooms, 0) AS double_vac_rooms, " +
	               "COALESCE(r_stats.double_occ_rooms, 0) AS double_occ_rooms, " +
	               "COALESCE(r_stats.double_part_vac_rooms, 0) AS double_part_vac_rooms, " +
	               "COALESCE(r_stats.double_part_vac_beds, 0) AS double_part_vac_beds, " +
	               "COALESCE(r_stats.double_over_rooms, 0) AS double_over_rooms, " +
	               "COALESCE(r_stats.double_over_seats, 0) AS double_over_seats, " +
	               "COALESCE(r_stats.triple_rooms, 0) AS triple_rooms, " +
	               "COALESCE(r_stats.triple_cap, 0) AS triple_cap, " +
	               "COALESCE(r_stats.triple_occ, 0) AS triple_occ, " +
	               "COALESCE(r_stats.triple_vac_rooms, 0) AS triple_vac_rooms, " +
	               "COALESCE(r_stats.triple_occ_rooms, 0) AS triple_occ_rooms, " +
	               "COALESCE(r_stats.triple_part_vac_rooms, 0) AS triple_part_vac_rooms, " +
	               "COALESCE(r_stats.triple_part_vac_beds, 0) AS triple_part_vac_beds, " +
	               "COALESCE(r_stats.triple_over_rooms, 0) AS triple_over_rooms, " +
	               "COALESCE(r_stats.triple_over_seats, 0) AS triple_over_seats, " +
	               "COALESCE(r_stats.quad_rooms, 0) AS quad_rooms, " +
	               "COALESCE(r_stats.quad_cap, 0) AS quad_cap, " +
	               "COALESCE(r_stats.quad_occ, 0) AS quad_occ, " +
	               "COALESCE(r_stats.quad_vac_rooms, 0) AS quad_vac_rooms, " +
	               "COALESCE(r_stats.quad_occ_rooms, 0) AS quad_occ_rooms, " +
	               "COALESCE(r_stats.quad_part_vac_rooms, 0) AS quad_part_vac_rooms, " +
	               "COALESCE(r_stats.quad_part_vac_beds, 0) AS quad_part_vac_beds, " +
	               "COALESCE(r_stats.quad_over_rooms, 0) AS quad_over_rooms, " +
	               "COALESCE(r_stats.quad_over_seats, 0) AS quad_over_seats, " +
	               "COALESCE(r_stats.dorm_rooms, 0) AS dorm_rooms, " +
	               "COALESCE(r_stats.dorm_cap, 0) AS dorm_cap, " +
	               "COALESCE(r_stats.dorm_occ, 0) AS dorm_occ, " +
	               "COALESCE(r_stats.dorm_vac_rooms, 0) AS dorm_vac_rooms, " +
	               "COALESCE(r_stats.dorm_occ_rooms, 0) AS dorm_occ_rooms, " +
	               "COALESCE(r_stats.dorm_part_vac_rooms, 0) AS dorm_part_vac_rooms, " +
	               "COALESCE(r_stats.dorm_part_vac_beds, 0) AS dorm_part_vac_beds, " +
	               "COALESCE(r_stats.dorm_over_rooms, 0) AS dorm_over_rooms, " +
	               "COALESCE(r_stats.dorm_over_seats, 0) AS dorm_over_seats, " +
	               "COALESCE(r_stats.pd_rooms, 0) AS pd_rooms, " +
	               "COALESCE(r_stats.pd_cap, 0) AS pd_cap, " +
	               "COALESCE(r_stats.pd_occ, 0) AS pd_occ, " +
	               "COALESCE(r_stats.pd_vac_rooms, 0) AS pd_vac_rooms, " +
	               "COALESCE(r_stats.pd_occ_rooms, 0) AS pd_occ_rooms, " +
	               "COALESCE(r_stats.pd_part_vac_rooms, 0) AS pd_part_vac_rooms, " +
	               "COALESCE(r_stats.pd_part_vac_beds, 0) AS pd_part_vac_beds, " +
	               "COALESCE(r_stats.pd_over_rooms, 0) AS pd_over_rooms, " +
	               "COALESCE(r_stats.pd_over_seats, 0) AS pd_over_seats, " +
	               "COALESCE(r_stats.guest_rooms, 0) AS guest_rooms, " +
	               "COALESCE(r_stats.guest_cap, 0) AS guest_cap, " +
	               "COALESCE(r_stats.guest_occ, 0) AS guest_occ, " +
	               "COALESCE(r_stats.guest_vac_rooms, 0) AS guest_vac_rooms, " +
	               "COALESCE(r_stats.guest_occ_rooms, 0) AS guest_occ_rooms, " +
	               "COALESCE(r_stats.guest_part_vac_rooms, 0) AS guest_part_vac_rooms, " +
	               "COALESCE(r_stats.guest_part_vac_beds, 0) AS guest_part_vac_beds, " +
	               "COALESCE(r_stats.guest_over_rooms, 0) AS guest_over_rooms, " +
	               "COALESCE(r_stats.guest_over_seats, 0) AS guest_over_seats, " +
	               "COALESCE(r_stats.icsr_rooms, 0) AS icsr_rooms, " +
	               "COALESCE(r_stats.icsr_cap, 0) AS icsr_cap, " +
	               "COALESCE(r_stats.icsr_occ, 0) AS icsr_occ, " +
	               "COALESCE(r_stats.icsr_vac_rooms, 0) AS icsr_vac_rooms, " +
	               "COALESCE(r_stats.icsr_occ_rooms, 0) AS icsr_occ_rooms, " +
	               "COALESCE(r_stats.icsr_part_vac_rooms, 0) AS icsr_part_vac_rooms, " +
	               "COALESCE(r_stats.icsr_part_vac_beds, 0) AS icsr_part_vac_beds, " +
	               "COALESCE(r_stats.icsr_over_rooms, 0) AS icsr_over_rooms, " +
	               "COALESCE(r_stats.icsr_over_seats, 0) AS icsr_over_seats, " +
	               "COALESCE(r_stats.official_rooms, 0) AS official_rooms, " +
	               "COALESCE(r_stats.official_cap, 0) AS official_cap, " +
	               "COALESCE(r_stats.official_occ, 0) AS official_occ, " +
	               "COALESCE(r_stats.official_vac_rooms, 0) AS official_vac_rooms, " +
	               "COALESCE(r_stats.official_occ_rooms, 0) AS official_occ_rooms, " +
	               "COALESCE(r_stats.official_part_vac_rooms, 0) AS official_part_vac_rooms, " +
	               "COALESCE(r_stats.official_part_vac_beds, 0) AS official_part_vac_beds, " +
	               "COALESCE(r_stats.official_over_rooms, 0) AS official_over_rooms, " +
	               "COALESCE(r_stats.official_over_seats, 0) AS official_over_seats, " +
	               "COALESCE(r_stats.partially_vacant_rooms, 0) AS partially_vacant_rooms, " +
	               "COALESCE(r_stats.partially_vacant_beds, 0) AS partially_vacant_beds, " +
	               "COALESCE(r_stats.overloaded_rooms, 0) AS overloaded_rooms, " +
	               "COALESCE(r_stats.overloaded_seats, 0) AS overloaded_seats " +
	               "FROM schooldev.dost_hostel_name hm " +
	               "LEFT JOIN ( " +
	               "    SELECT " +
	               "        hfm.hostel_id, " +
	               "        SUM(ri.capacity) AS total_capacity, " +
	               "        SUM(COALESCE(hra_cnt.occ_count, 0)) AS total_utilized, " +
	               "        SUM(CASE WHEN ri.capacity = 1 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') THEN 1 ELSE 0 END) AS single_rooms, " +
	               "        SUM(CASE WHEN ri.capacity = 1 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') THEN ri.capacity ELSE 0 END) AS single_cap, " +
	               "        SUM(CASE WHEN ri.capacity = 1 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') THEN COALESCE(hra_cnt.occ_count, 0) ELSE 0 END) AS single_occ, " +
	               "        SUM(CASE WHEN ri.capacity = 1 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) = 0 THEN 1 ELSE 0 END) AS single_vac_rooms, " +
	               "        SUM(CASE WHEN ri.capacity = 1 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) > 0 THEN 1 ELSE 0 END) AS single_occ_rooms, " +
	               "        0 AS single_part_vac_rooms, 0 AS single_part_vac_beds, " +
	               "        SUM(CASE WHEN ri.capacity = 1 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) > 1 THEN 1 ELSE 0 END) AS single_over_rooms, " +
	               "        SUM(CASE WHEN ri.capacity = 1 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) > 1 THEN (COALESCE(hra_cnt.occ_count, 0) - 1) ELSE 0 END) AS single_over_seats, " +
	               "        SUM(CASE WHEN ri.capacity = 2 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') THEN 1 ELSE 0 END) AS double_rooms, " +
	               "        SUM(CASE WHEN ri.capacity = 2 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') THEN ri.capacity ELSE 0 END) AS double_cap, " +
	               "        SUM(CASE WHEN ri.capacity = 2 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') THEN COALESCE(hra_cnt.occ_count, 0) ELSE 0 END) AS double_occ, " +
	               "        SUM(CASE WHEN ri.capacity = 2 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) = 0 THEN 1 ELSE 0 END) AS double_vac_rooms, " +
	               "        SUM(CASE WHEN ri.capacity = 2 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) > 0 THEN 1 ELSE 0 END) AS double_occ_rooms, " +
	               "        SUM(CASE WHEN ri.capacity = 2 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) = 1 THEN 1 ELSE 0 END) AS double_part_vac_rooms, " +
	               "        SUM(CASE WHEN ri.capacity = 2 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) = 1 THEN 1 ELSE 0 END) AS double_part_vac_beds, " +
	               "        SUM(CASE WHEN ri.capacity = 2 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) > 2 THEN 1 ELSE 0 END) AS double_over_rooms, " +
	               "        SUM(CASE WHEN ri.capacity = 2 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) > 2 THEN (COALESCE(hra_cnt.occ_count, 0) - 2) ELSE 0 END) AS double_over_seats, " +
	               "        SUM(CASE WHEN ri.capacity = 3 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') THEN 1 ELSE 0 END) AS triple_rooms, " +
	               "        SUM(CASE WHEN ri.capacity = 3 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') THEN ri.capacity ELSE 0 END) AS triple_cap, " +
	               "        SUM(CASE WHEN ri.capacity = 3 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') THEN COALESCE(hra_cnt.occ_count, 0) ELSE 0 END) AS triple_occ, " +
	               "        SUM(CASE WHEN ri.capacity = 3 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) = 0 THEN 1 ELSE 0 END) AS triple_vac_rooms, " +
	               "        SUM(CASE WHEN ri.capacity = 3 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) > 0 THEN 1 ELSE 0 END) AS triple_occ_rooms, " +
	               "        SUM(CASE WHEN ri.capacity = 3 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) > 0 AND COALESCE(hra_cnt.occ_count, 0) < 3 THEN 1 ELSE 0 END) AS triple_part_vac_rooms, " +
	               "        SUM(CASE WHEN ri.capacity = 3 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) > 0 AND COALESCE(hra_cnt.occ_count, 0) < 3 THEN (3 - COALESCE(hra_cnt.occ_count, 0)) ELSE 0 END) AS triple_part_vac_beds, " +
	               "        SUM(CASE WHEN ri.capacity = 3 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) > 3 THEN 1 ELSE 0 END) AS triple_over_rooms, " +
	               "        SUM(CASE WHEN ri.capacity = 3 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) > 3 THEN (COALESCE(hra_cnt.occ_count, 0) - 3) ELSE 0 END) AS triple_over_seats, " +
	               "        SUM(CASE WHEN ri.capacity = 4 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') THEN 1 ELSE 0 END) AS quad_rooms, " +
	               "        SUM(CASE WHEN ri.capacity = 4 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') THEN ri.capacity ELSE 0 END) AS quad_cap, " +
	               "        SUM(CASE WHEN ri.capacity = 4 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') THEN COALESCE(hra_cnt.occ_count, 0) ELSE 0 END) AS quad_occ, " +
	               "        SUM(CASE WHEN ri.capacity = 4 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) = 0 THEN 1 ELSE 0 END) AS quad_vac_rooms, " +
	               "        SUM(CASE WHEN ri.capacity = 4 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) > 0 THEN 1 ELSE 0 END) AS quad_occ_rooms, " +
	               "        SUM(CASE WHEN ri.capacity = 4 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) > 0 AND COALESCE(hra_cnt.occ_count, 0) < 4 THEN 1 ELSE 0 END) AS quad_part_vac_rooms, " +
	               "        SUM(CASE WHEN ri.capacity = 4 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) > 0 AND COALESCE(hra_cnt.occ_count, 0) < 4 THEN (4 - COALESCE(hra_cnt.occ_count, 0)) ELSE 0 END) AS quad_part_vac_beds, " +
	               "        SUM(CASE WHEN ri.capacity = 4 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) > 4 THEN 1 ELSE 0 END) AS quad_over_rooms, " +
	               "        SUM(CASE WHEN ri.capacity = 4 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) > 4 THEN (COALESCE(hra_cnt.occ_count, 0) - 4) ELSE 0 END) AS quad_over_seats, " +
	               "        SUM(CASE WHEN ri.capacity > 4 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') THEN 1 ELSE 0 END) AS dorm_rooms, " +
	               "        SUM(CASE WHEN ri.capacity > 4 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') THEN ri.capacity ELSE 0 END) AS dorm_cap, " +
	               "        SUM(CASE WHEN ri.capacity > 4 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') THEN COALESCE(hra_cnt.occ_count, 0) ELSE 0 END) AS dorm_occ, " +
	               "        SUM(CASE WHEN ri.capacity > 4 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) = 0 THEN 1 ELSE 0 END) AS dorm_vac_rooms, " +
	               "        SUM(CASE WHEN ri.capacity > 4 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) > 0 THEN 1 ELSE 0 END) AS dorm_occ_rooms, " +
	               "        SUM(CASE WHEN ri.capacity > 4 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) > 0 AND COALESCE(hra_cnt.occ_count, 0) < ri.capacity THEN 1 ELSE 0 END) AS dorm_part_vac_rooms, " +
	               "        SUM(CASE WHEN ri.capacity > 4 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) > 0 AND COALESCE(hra_cnt.occ_count, 0) < ri.capacity THEN (ri.capacity - COALESCE(hra_cnt.occ_count, 0)) ELSE 0 END) AS dorm_part_vac_beds, " +
	               "        SUM(CASE WHEN ri.capacity > 4 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) > ri.capacity THEN 1 ELSE 0 END) AS dorm_over_rooms, " +
	               "        SUM(CASE WHEN ri.capacity > 4 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') AND COALESCE(hra_cnt.occ_count, 0) > ri.capacity THEN (COALESCE(hra_cnt.occ_count, 0) - ri.capacity) ELSE 0 END) AS dorm_over_seats, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'pd' THEN 1 ELSE 0 END) AS pd_rooms, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'pd' THEN ri.capacity ELSE 0 END) AS pd_cap, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'pd' THEN COALESCE(hra_cnt.occ_count, 0) ELSE 0 END) AS pd_occ, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'pd' AND COALESCE(hra_cnt.occ_count, 0) = 0 THEN 1 ELSE 0 END) AS pd_vac_rooms, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'pd' AND COALESCE(hra_cnt.occ_count, 0) > 0 THEN 1 ELSE 0 END) AS pd_occ_rooms, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'pd' AND COALESCE(hra_cnt.occ_count, 0) > 0 AND COALESCE(hra_cnt.occ_count, 0) < ri.capacity THEN 1 ELSE 0 END) AS pd_part_vac_rooms, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'pd' AND COALESCE(hra_cnt.occ_count, 0) > 0 AND COALESCE(hra_cnt.occ_count, 0) < ri.capacity THEN (ri.capacity - COALESCE(hra_cnt.occ_count, 0)) ELSE 0 END) AS pd_part_vac_beds, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'pd' AND COALESCE(hra_cnt.occ_count, 0) > ri.capacity THEN 1 ELSE 0 END) AS pd_over_rooms, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'pd' AND COALESCE(hra_cnt.occ_count, 0) > ri.capacity THEN (COALESCE(hra_cnt.occ_count, 0) - ri.capacity) ELSE 0 END) AS pd_over_seats, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'guest' THEN 1 ELSE 0 END) AS guest_rooms, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'guest' THEN ri.capacity ELSE 0 END) AS guest_cap, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'guest' THEN COALESCE(hra_cnt.occ_count, 0) ELSE 0 END) AS guest_occ, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'guest' AND COALESCE(hra_cnt.occ_count, 0) = 0 THEN 1 ELSE 0 END) AS guest_vac_rooms, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'guest' AND COALESCE(hra_cnt.occ_count, 0) > 0 THEN 1 ELSE 0 END) AS guest_occ_rooms, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'guest' AND COALESCE(hra_cnt.occ_count, 0) > 0 AND COALESCE(hra_cnt.occ_count, 0) < ri.capacity THEN 1 ELSE 0 END) AS guest_part_vac_rooms, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'guest' AND COALESCE(hra_cnt.occ_count, 0) > 0 AND COALESCE(hra_cnt.occ_count, 0) < ri.capacity THEN (ri.capacity - COALESCE(hra_cnt.occ_count, 0)) ELSE 0 END) AS guest_part_vac_beds, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'guest' AND COALESCE(hra_cnt.occ_count, 0) > ri.capacity THEN 1 ELSE 0 END) AS guest_over_rooms, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'guest' AND COALESCE(hra_cnt.occ_count, 0) > ri.capacity THEN (COALESCE(hra_cnt.occ_count, 0) - ri.capacity) ELSE 0 END) AS guest_over_seats, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'icsr' THEN 1 ELSE 0 END) AS icsr_rooms, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'icsr' THEN ri.capacity ELSE 0 END) AS icsr_cap, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'icsr' THEN COALESCE(hra_cnt.occ_count, 0) ELSE 0 END) AS icsr_occ, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'icsr' AND COALESCE(hra_cnt.occ_count, 0) = 0 THEN 1 ELSE 0 END) AS icsr_vac_rooms, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'icsr' AND COALESCE(hra_cnt.occ_count, 0) > 0 THEN 1 ELSE 0 END) AS icsr_occ_rooms, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'icsr' AND COALESCE(hra_cnt.occ_count, 0) > 0 AND COALESCE(hra_cnt.occ_count, 0) < ri.capacity THEN 1 ELSE 0 END) AS icsr_part_vac_rooms, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'icsr' AND COALESCE(hra_cnt.occ_count, 0) > 0 AND COALESCE(hra_cnt.occ_count, 0) < ri.capacity THEN (ri.capacity - COALESCE(hra_cnt.occ_count, 0)) ELSE 0 END) AS icsr_part_vac_beds, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'icsr' AND COALESCE(hra_cnt.occ_count, 0) > ri.capacity THEN 1 ELSE 0 END) AS icsr_over_rooms, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'icsr' AND COALESCE(hra_cnt.occ_count, 0) > ri.capacity THEN (COALESCE(hra_cnt.occ_count, 0) - ri.capacity) ELSE 0 END) AS icsr_over_seats, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'official' THEN 1 ELSE 0 END) AS official_rooms, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'official' THEN ri.capacity ELSE 0 END) AS official_cap, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'official' THEN COALESCE(hra_cnt.occ_count, 0) ELSE 0 END) AS official_occ, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'official' AND COALESCE(hra_cnt.occ_count, 0) = 0 THEN 1 ELSE 0 END) AS official_vac_rooms, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'official' AND COALESCE(hra_cnt.occ_count, 0) > 0 THEN 1 ELSE 0 END) AS official_occ_rooms, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'official' AND COALESCE(hra_cnt.occ_count, 0) > 0 AND COALESCE(hra_cnt.occ_count, 0) < ri.capacity THEN 1 ELSE 0 END) AS official_part_vac_rooms, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'official' AND COALESCE(hra_cnt.occ_count, 0) > 0 AND COALESCE(hra_cnt.occ_count, 0) < ri.capacity THEN (ri.capacity - COALESCE(hra_cnt.occ_count, 0)) ELSE 0 END) AS official_part_vac_beds, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'official' AND COALESCE(hra_cnt.occ_count, 0) > ri.capacity THEN 1 ELSE 0 END) AS official_over_rooms, " +
	               "        SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'official' AND COALESCE(hra_cnt.occ_count, 0) > ri.capacity THEN (COALESCE(hra_cnt.occ_count, 0) - ri.capacity) ELSE 0 END) AS official_over_seats, " +
	               "        SUM(CASE WHEN ri.capacity > COALESCE(hra_cnt.occ_count, 0) AND COALESCE(hra_cnt.occ_count, 0) > 0 THEN 1 ELSE 0 END) AS partially_vacant_rooms, " +
	               "        SUM(CASE WHEN ri.capacity > COALESCE(hra_cnt.occ_count, 0) AND COALESCE(hra_cnt.occ_count, 0) > 0 THEN (ri.capacity - COALESCE(hra_cnt.occ_count, 0)) ELSE 0 END) AS partially_vacant_beds, " +
	               "        SUM(CASE WHEN COALESCE(hra_cnt.occ_count, 0) > ri.capacity THEN 1 ELSE 0 END) AS overloaded_rooms, " +
	               "        SUM(CASE WHEN COALESCE(hra_cnt.occ_count, 0) > ri.capacity THEN (COALESCE(hra_cnt.occ_count, 0) - ri.capacity) ELSE 0 END) AS overloaded_seats " +
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
	List<Object[]> getHostelCapacityListByDateRange(@Param("fromDate") String fromDate, @Param("toDate") String toDate);

	@Query(value = "SELECT " +
	               "hm.hostel_id, " +
	               "hm.hostel_name, " +
	               "UPPER(SUBSTRING(hra.student_id FROM 1 FOR 2)) AS course_code, " +
	               "COALESCE(cm.course_master_name, UPPER(SUBSTRING(hra.student_id FROM 1 FOR 2))) AS course_name, " +
	               "CASE WHEN SUBSTRING(hra.student_id FROM 3 FOR 2) ~ '^[0-9]{2}$' THEN '20' || SUBSTRING(hra.student_id FROM 3 FOR 2) ELSE 'Other' END AS batch_year, " +
	               "COUNT(hra.room_allotment_id) AS student_count " +
	               "FROM schooldev.dost_hostel_name hm " +
	               "JOIN schooldev.\"HOSTEL_FLOOR_MASTER\" hfm ON hfm.hostel_id = hm.hostel_id " +
	               "JOIN schooldev.\"HOSTEL_ROOM_INFO\" ri ON (ri.building_id = hfm.floor_id AND ri.active_flag = 'Y') " +
	               "JOIN schooldev.\"HOSTEL_ROOM_ALLOTMENT_INFO\" hra ON (hra.room_id = ri.room_id AND hra.active_flag = 'Y' AND hra.vacate_date IS NULL AND hra.shifted_date IS NULL) " +
	               "LEFT JOIN schooldev.\"COURSE_ALLOCATION_INFO\" cai ON (cai.student_id = hra.student_id AND cai.active_flag = 'Y') " +
	               "LEFT JOIN schooldev.course_master cm ON (cm.course_master_id = cai.course_id AND cm.active_flag = 'Y') " +
	               "WHERE hm.active_flag = 'Y' AND (:hostelId = 0 OR hm.hostel_id = :hostelId) " +
	               "GROUP BY hm.hostel_id, hm.hostel_name, course_code, course_name, batch_year " +
	               "ORDER BY hm.hostel_name, course_name, batch_year DESC", nativeQuery = true)
	List<Object[]> getHostelStudentDistributionByDateRange(@Param("hostelId") Long hostelId, @Param("fromDate") String fromDate, @Param("toDate") String toDate);

	@Query(value = "SELECT " +
	               "hm.hostel_id, " +
	               "hm.hostel_name, " +
	               "hfm.floor_name, " +
	               "ri.room_no, " +
	               "ri.capacity, " +
	               "LOWER(COALESCE(ri.official_guest_status, 'guest')) AS guest_type, " +
	               "CASE WHEN hra.room_allotment_id IS NOT NULL THEN 'Occupied' ELSE 'Vacant' END AS room_status, " +
	               "COALESCE(hra.student_id, 'Guest') AS occupant_info " +
	               "FROM schooldev.dost_hostel_name hm " +
	               "JOIN schooldev.\"HOSTEL_FLOOR_MASTER\" hfm ON hfm.hostel_id = hm.hostel_id " +
	               "JOIN schooldev.\"HOSTEL_ROOM_INFO\" ri ON (ri.building_id = hfm.floor_id AND ri.active_flag = 'Y') " +
	               "LEFT JOIN schooldev.\"HOSTEL_ROOM_ALLOTMENT_INFO\" hra ON (hra.room_id = ri.room_id AND hra.active_flag = 'Y' AND hra.vacate_date IS NULL AND hra.shifted_date IS NULL) " +
	               "WHERE hm.active_flag = 'Y' AND (:hostelId = 0 OR hm.hostel_id = :hostelId) " +
	               "AND LOWER(COALESCE(ri.official_guest_status, '')) IN ('guest', 'official', 'icsr') " +
	               "ORDER BY hm.hostel_name, hfm.floor_name, ri.room_no", nativeQuery = true)
	List<Object[]> getLiveGuestRoomDetailsList(@Param("hostelId") Long hostelId);

	@Query(value = "SELECT " +
	               "hm.hostel_id, " +
	               "COALESCE(SUM(CASE WHEN COALESCE(hpnp.room_capacity, ri.capacity) = 1 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') THEN 1 ELSE 0 END), 0) AS single_phys, " +
	               "COALESCE(SUM(CASE WHEN COALESCE(hpnp.room_capacity, ri.capacity) = 2 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') THEN 1 ELSE 0 END), 0) AS double_phys, " +
	               "COALESCE(SUM(CASE WHEN COALESCE(hpnp.room_capacity, ri.capacity) = 3 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') THEN 1 ELSE 0 END), 0) AS triple_phys, " +
	               "COALESCE(SUM(CASE WHEN COALESCE(hpnp.room_capacity, ri.capacity) = 4 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') THEN 1 ELSE 0 END), 0) AS quad_phys, " +
	               "COALESCE(SUM(CASE WHEN COALESCE(hpnp.room_capacity, ri.capacity) > 4 AND LOWER(COALESCE(ri.official_guest_status, '')) NOT IN ('guest', 'official', 'icsr', 'pd') THEN 1 ELSE 0 END), 0) AS dorm_phys, " +
	               "COALESCE(SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'pd' THEN 1 ELSE 0 END), 0) AS pd_phys, " +
	               "COALESCE(SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'guest' THEN 1 ELSE 0 END), 0) AS guest_phys, " +
	               "COALESCE(SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'icsr' THEN 1 ELSE 0 END), 0) AS icsr_phys, " +
	               "COALESCE(SUM(CASE WHEN LOWER(COALESCE(ri.official_guest_status, '')) = 'official' THEN 1 ELSE 0 END), 0) AS official_phys, " +
	               "COALESCE(COUNT(DISTINCT hpnp.id), 0) AS total_phys " +
	               "FROM schooldev.dost_hostel_name hm " +
	               "JOIN schooldev.\"HOSTEL_FLOOR_MASTER\" hfm ON hfm.hostel_id = hm.hostel_id AND hfm.active_flag = 'Y' " +
	               "JOIN schooldev.\"HOSTEL_ROOM_INFO\" ri ON (ri.building_id = hfm.floor_id AND ri.active_flag = 'Y') " +
	               "JOIN ( " +
	               "    SELECT room_id, COUNT(room_allotment_id) AS active_occupants " +
	               "    FROM schooldev.\"HOSTEL_ROOM_ALLOTMENT_INFO\" " +
	               "    WHERE active_flag = 'Y' AND vacate_date IS NULL AND shifted_date IS NULL " +
	               "    GROUP BY room_id " +
	               ") room_occ ON room_occ.room_id = ri.room_id AND room_occ.active_occupants > ri.capacity " +
	               "JOIN schooldev.\"HOSTEL_ROOM_ALLOTMENT_INFO\" hra ON (hra.room_id = ri.room_id AND hra.active_flag = 'Y' AND hra.vacate_date IS NULL AND hra.shifted_date IS NULL) " +
	               "JOIN schooldev.hostel_physically_not_present_students hpnp ON hpnp.roll_number = hra.student_id " +
	               "WHERE hm.active_flag = 'Y' " +
	               "GROUP BY hm.hostel_id", nativeQuery = true)
	List<Object[]> getPhysicallyUnavailableOverloadCountByHostel();
}
