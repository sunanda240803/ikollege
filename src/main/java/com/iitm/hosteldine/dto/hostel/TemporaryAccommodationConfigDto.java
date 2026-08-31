package com.iitm.hosteldine.dto.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.model.hostel.TemporaryAccommodationConfigEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO for {@link TemporaryAccommodationConfigEntity}
 */
@Data
@Builder
public class TemporaryAccommodationConfigDto {
    private Long id;
    private String categoryName;
    private String description;
    private Integer accStayAmnt;
    private Integer breakfastCoupon;
    private Integer lunchCoupon;
    private Integer dinnerCoupon;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate effectiveDate;
    private Integer rebateCharges;
}