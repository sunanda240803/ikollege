package com.iitm.hosteldine.dto.hostel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GuestCouponIssuedDTO {
    private Integer couponId;
    private Long requestId;
    private String couponNumber;
    private String name;
    private String couponType;
    private String usedStatus;
    private String messName;
    private String diningFrom;
    private String diningTo;
    private String submittedFrom;
    private String submittedTo;
    private Integer messId;
    private String userRole;
    private String loggedInUser;
    private String status;
    private List errorList;
    private int bfCount;
    private int lcCount;
    private int drCount;
}
