package com.iitm.hosteldine.repository.hostel;

import com.iitm.hosteldine.dto.hostel.GuestCouponConfigDto;
import com.iitm.hosteldine.model.hostel.GuestCouponConfigEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GuestCouponConfigRepository extends JpaRepository<GuestCouponConfigEntity, Long> {
	List<GuestCouponConfigEntity> findByCategory(String category);

	Page<GuestCouponConfigEntity> findAllByActiveFlag(String statusActive, Pageable pageable);

	Optional<GuestCouponConfigEntity> findByIdAndActiveFlag(long id, String statusActive);

	@Query(" SELECT gce FROM GuestCouponConfigEntity gce WHERE gce.activeFlag = :statusActive AND ( " +
			" gce.category ILIKE CONCAT('%', :search, '%') OR " +
			" gce.description ILIKE CONCAT('%', :search, '%'))")
	Page<GuestCouponConfigEntity> findByGuestCouponConfigSearchList(String statusActive, Pageable pageable, String search);

	@Query("""
			select new com.iitm.hosteldine.dto.hostel.GuestCouponConfigDto(effectiveDate, validToDate, breakfastAmount, lunchAmount, dinnerAmount,snacksAmount, category) \s
			from GuestCouponConfigEntity\s
			where effectiveDate <= :fromDate and :toDate <= validToDate\s
			and activeFlag = 'Y' and category = :category order by createdAt desc limit 1
			""")
	GuestCouponConfigDto getCouponRates(LocalDate fromDate, LocalDate toDate, String category);
}