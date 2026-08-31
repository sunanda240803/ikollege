package com.iitm.hosteldine.dto.hostel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestCouponIssuedResultDTO {
    private Integer couponId;
    private Integer requestId;
    private String couponNumber;
    private String name;
    private String validityFromDate;
    private String validityToDate;
    private String couponType;
    private String usedStatus;
    private String submittedDate;
    private String messName;
    private String studentId;
}
