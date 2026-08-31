package com.iitm.hosteldine.repository.hostel;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.hostel.HostelMasterEntity;
import com.iitm.hosteldine.model.hostel.HostelUserMappingEntity;
import com.iitm.hosteldine.model.hostel.HostelUserMappingId;

public interface HostelUserMappingRepository extends JpaRepository<HostelUserMappingEntity, HostelUserMappingId> {
	Optional<HostelUserMappingEntity> findByIdAndActiveFlag(Long id, String activeFlag);

	List<HostelUserMappingEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

	Optional<HostelUserMappingEntity> findByIdHostelIdAndIdUserAndActiveFlag(Long id, String userName,
			String activeFlag);

	boolean existsByActiveFlagAndIdUser(String statusActive, String userName);

	Page<HostelUserMappingEntity> findAllByActiveFlag(String statusActive, Pageable pageable);

	@Query("SELECT e FROM HostelUserMappingEntity e join HostelMasterEntity hme on (e.id.hostel.id=hme.id ) "
			+ "WHERE e.activeFlag = :statusActive AND (" 
			+ "hme.hostelName ILIKE CONCAT('%', :search, '%') OR "
			+ "e.id.user ILIKE CONCAT('%', :search, '%'))")
	Page<HostelUserMappingEntity> findByHostelUserMappingSearchList(String statusActive, Pageable pageable,
			String search);

	@Query("""
        SELECT h.id.hostel FROM HostelUserMappingEntity h
        WHERE h.activeFlag = :statusActive and h.id.user = :userId
    """)
	List<HostelMasterEntity> getHostelUserMappingDetails(String userId, String statusActive);

	Optional<HostelUserMappingEntity> findFirstByIdUserAndActiveFlagOrderByCreatedAtDesc(String userName, String statusActive);

	@Query(value = """
			select hostel_id, hostel_name,count(distinct(room_no))as total_rooms,
				   sum (case when official_guest_status::text = '0' then 1 else 0 end) as general,
				   sum (case when official_guest_status::text = 'official' then 1 else 0 end) as official,
				   sum (case when official_guest_status::text = 'guest' then 1 else 0 end) as guest,
				   sum (case when official_guest_status::text = 'ICSR' then 1 else 0 end) as icsr,
				   sum (case when official_guest_status::text = 'notfit' then 1 else 0 end) as notfit,
				   sum (case when official_guest_status::text = 'pd' then 1 else 0 end) as pd,
				   sum(case when (official_guest_status='0' OR official_guest_status = 'notfit' OR official_guest_status = 'pd')
								then (case when vacancy<=0 then 1 else 0 end) else 0 end ) as rooms_alloted,
				   sum(case when (official_guest_status='0' OR official_guest_status = 'notfit' OR official_guest_status = 'pd')
								then (case when vacancy>=1 then 1 else 0 end) else 0 end) as rooms_vacant,
				   sum(case when (official_guest_status='0' OR official_guest_status = 'notfit' OR official_guest_status = 'pd')
								then TOTAL else 0 end) as total_no_of_seats,
				   sum(case when (official_guest_status='0' OR official_guest_status= 'notfit' OR official_guest_status = 'pd')
								then occupied else 0 end ) as alloted_seats,
				   sum(case when (official_guest_status='0' OR official_guest_status= 'notfit' OR official_guest_status = 'pd')
								then (case when vacancy<0 then 0 else vacancy end) else 0 end )as seats_vacant
				   from (select * from schooldev."ROOM_OCCUPANCY_STATUS_VIEW_ALL_TYPE_ROOMS")as occupancy_status
				   where  hostel_id= :hostelId group by hostel_id,hostel_name order by hostel_name
	""", nativeQuery = true)
	Optional<Object> getRoomDetails(Long hostelId);

	@Query(value = """
			select a.accomm_type,
				  case when a.student_type is null then :hyphen else a.student_type end as student_type ,
				  case when alloted_total is null then 0 else alloted_total end as alloted_total,
				  case when checked_in is null then 0 else checked_in end as checked_in,
				  case when pending_checkin is null then 0 else pending_checkin end as pending_checkin,
				  case when tobe_check_out_today is null then 0 else tobe_check_out_today end as tobe_check_out_today,
				  case when checked_out is null then 0 else checked_out end as checked_out_today,
				  case when pending_checkout is null then 0 else pending_checkout end as pending_checkout
		   			from schooldev.total_summary_count(:userName) a
					left join schooldev.pending_checkout_count(:userName) b on (a.accomm_type= b.accomm_type and a.student_type=b.student_type)
		    order by a.accomm_type
	""", nativeQuery = true)
	Optional<List<Object>> getAccommodationCountList(String userName, String hyphen);

	@Query(value = """
		select count(*) as total_count from schooldev.student_vacating_hostel('null', 'null', null, 'null', 'null', 0, null, null,
		:approvalStatus, :userRole, 1, null, :userName)
	""", nativeQuery = true)
	Optional<Long> getVacatingCount(String userRole, String userName, String approvalStatus);

	@Query(value = "select count(distinct student_id) from schooldev.hostel_enrollment_list(:approvalStatus, null, null, '0', :userRole, :userName)", nativeQuery = true)
	Optional<Long> getHostelEnrollmentCount(String approvalStatus, String userRole, String userName, Long hostelId);


}