package com.iitm.hosteldine.constant.dashboard.student;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum CategoryCode {
    VACATING_HOSTEL("VACATING-HOSTEL");
    private final String categoryCode;
    CategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }
}
