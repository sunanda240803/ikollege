package com.iitm.hosteldine.repository.student;

import com.iitm.hosteldine.dto.ConvocationReportRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.student.ConvocationAdditionalCouponsEntity;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConvocationAdditionalCouponsRepository extends JpaRepository<ConvocationAdditionalCouponsEntity, Long> {
    Optional<ConvocationAdditionalCouponsEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    List<ConvocationAdditionalCouponsEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    @Query(value = """
                select count(x) from ConvocationAdditionalCouponsEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);

    @Query("""
select new com.iitm.hosteldine.dto.ConvocationReportRow(
    x.id,
    c.createdAt,
    x.diningDate,
    x.noOfBreakfastCoupon,
    x.noOfLunchCoupon,
    x.noOfDinnerCoupon,
	    c.id,
	    c.studentId,
	    c.studentName,
	    c.gender,
	    c.mailId,
	        c.menuType,
    c.accommodationStatus,
    c.complimentaryCoupons,
    c.additionalNoOfCoupons,
    c.overallAmount,
    c.paymentStatus,
    c.hostelName
)
from ConvocationAccommodationEntity c
left join ConvocationAdditionalCouponsEntity x
    on x.convocation.id = c.id
    and x.activeFlag = :activeFlag
where c.activeFlag = :activeFlag
  and c.createdAt >= :fromDateTime
  and c.createdAt < :toDateTime
  and (
      upper(c.paymentStatus) = upper(:paymentStatus)
      or (:includeNullPaymentStatus = true and c.paymentStatus is null)
  )
order by c.createdAt desc
""")
    List<ConvocationReportRow> getConvocationAdditionalCoupons(
            @Param("activeFlag") String activeFlag,
            @Param("fromDateTime") LocalDateTime fromDateTime,
            @Param("toDateTime") LocalDateTime toDateTime,
            @Param("paymentStatus") String paymentStatus,
            @Param("includeNullPaymentStatus") boolean includeNullPaymentStatus
    );
}
