package com.iitm.hosteldine.constant.college;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum CollegeInfoContents {
    SCHOOL_GEOGRAPHY_INFO_FORM_KEY("schoolGeographyInfoDto");
    private final String constants;
    CollegeInfoContents(String constants) {
        this.constants = constants;
    }
}
