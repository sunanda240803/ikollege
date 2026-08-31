package com.iitm.hosteldine.constant.hostel;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum CouponStatus {
    DISTRIBUTED("Distributed"),
    RETURNED("Returned"),
    USED("Used");

    private final String status;

    CouponStatus(String status) {
        this.status = status;
    }
}
