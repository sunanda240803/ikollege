package com.iitm.hosteldine.repository.warden;



import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.warden.GuestAccommodationChargesEntity;

public interface GuestAccommodationChargesRepository extends JpaRepository<GuestAccommodationChargesEntity, Long> {
    Optional<GuestAccommodationChargesEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    List<GuestAccommodationChargesEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    @Query(value = """
                select count(x) from GuestAccommodationChargesEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);

	Page<GuestAccommodationChargesEntity> findAllByActiveFlag(String statusActive,
			Pageable pageable);

	 @Query(value = """
		        SELECT * 
		        FROM schooldev."GUEST_ACCOMMODATION_CHARGES"
		        WHERE from_date::date <= now()::date
		          AND (to_date::date IS NULL OR to_date::date >= now()::date)
		          AND active_flag = :statusActive
		        ORDER BY id DESC
		        LIMIT 1
		        """, nativeQuery = true)
	Optional<GuestAccommodationChargesEntity> getAccommodationCharges(String statusActive);
	 
	 @Query(value = """
		        SELECT * FROM schooldev."GUEST_ACCOMMODATION_CHARGES"
		        WHERE (from_date <= CAST(:fromDate AS date))
		          AND (to_date IS NULL OR CAST(:toDate AS date) <= to_date)
		          AND active_flag = :statusActive
		        ORDER BY created_at DESC
		        LIMIT 1
		        """, nativeQuery = true)
	GuestAccommodationChargesEntity findGuestAccommodationChargesDate(LocalDate fromDate, LocalDate toDate,
			String statusActive);

	
}