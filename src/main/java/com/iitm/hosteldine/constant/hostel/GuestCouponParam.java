package com.iitm.hosteldine.constant.hostel;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum GuestCouponParam {
    NAME("name"),
    REQUEST_ID("requestId"),
    DINING_FROM("diningFrom"),
    DINING_TO("diningTo"),
    SUBMITTED_FROM("submittedFrom"),
    SUBMITTED_TO("submittedTo"),
    MESS_ID("messId"),
    USED_STATUS("usedStatus");

    private final String key;

    GuestCouponParam(String key) {
        this.key = key;
    }

}
