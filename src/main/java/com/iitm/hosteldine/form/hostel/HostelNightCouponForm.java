package com.iitm.hosteldine.form.hostel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HostelNightCouponForm {
    private int vegCount;
    private int nonVegCount;
    private Boolean isLedger;
    private String error;
    private String payBy;
}