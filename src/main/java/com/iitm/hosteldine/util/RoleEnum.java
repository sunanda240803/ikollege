package com.iitm.hosteldine.util;

import lombok.Getter;

@Getter
public enum RoleEnum {

    CCW_DEAN("CCW Dean"),
    CCW_OFFICE("CCW Office"),
    HOSTEL_CHECK_IN("Hostel Check In"), 
    DEAN("Dean"), 
    WARDEN("Warden"),
    HM_OFFICE("HM Office"),
    DOST_DEAN("DOST Dean"),
    ICSR_DEAN("ICSR Dean"),
    OFFICE("Office"),
    GUEST_ALLOTMENT("Guest Allotment"),
    FACULTY("Faculty"),
    SOFTWARE_ADMIN("SoftwareAdmin"),
    CATERER("Caterer");

    private final String value;
    RoleEnum(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
