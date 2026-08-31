package com.iitm.hosteldine.dto.hostel;

import com.iitm.hosteldine.constant.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestCouponConfigDto {

    private Long id; // ID of the configuration (mapped to configId in the entity)

    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate effectiveDate; // Effective start date of the coupon

    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate validToDate; // Valid until date

    private Integer breakfastAmount; // Amount allocated for breakfast

    private Integer lunchAmount; // Amount allocated for lunch

    private Integer dinnerAmount; // Amount allocated for dinner

    private Integer snacksAmount; // Amount allocated for Snacks

    private String description; // Description of the coupon

    private String activeFlag; // Active status ('Y' or 'N')

    private Integer validityDays; // Number of validity days for the coupon

    private String category; // Coupon category (e.g., General, Project Staff, etc.)

    public GuestCouponConfigDto(LocalDate effectiveDate, LocalDate validToDate, Integer breakfastAmount,
                                Integer lunchAmount, Integer dinnerAmount,Integer snacksAmount, String category) {
        this.effectiveDate = effectiveDate;
        this.validToDate = validToDate;
        this.breakfastAmount = breakfastAmount;
        this.lunchAmount = lunchAmount;
        this.dinnerAmount = dinnerAmount;
        this.snacksAmount = snacksAmount;
        this.category = category;
    }
}
