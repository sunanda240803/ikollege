package com.iitm.hosteldine.util;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum HostelIndividualAllotmentEnum {
    STUDENT_NOT_EXIST("studentNotExist"),
    SETTLEMENT_COMPLETED("settlementCompleted"),
    NOT_EXIST("notExist"),
    EXIST("exist"),
    GENDER_FAIL("genderFail"),
    VACATING_FORM_APPROVED("vacatingFormApproved"),
    SWAP("swap"),
    ALREADY_ALLOTTED("alreadyAllotted");

    private final String value;
    HostelIndividualAllotmentEnum(String value) {
        this.value = value;
    }

}
