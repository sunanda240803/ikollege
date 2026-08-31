package com.iitm.hosteldine.dto.student;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class ConvocationAdditionalCouponsDto {
    private Long id;
    private ConvocationAccommodationDto convocation;
    private LocalDate diningDate;
    private List<ConvocationAdditionalCouponsDto> additionalCoupons;
    private Integer noOfBreakfastCoupon;
    private Integer noOfLunchCoupon;
    private Integer noOfDinnerCoupon;
    private String bf;
    private String ln;
    private String dn;
}