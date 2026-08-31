package com.iitm.hosteldine.constant;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum CategoryEnum {

	DOST("DOST"),
	ICSR("ICSR"),
	INTERVIEWS("INTERVIEWS"), 
	INTERNSHIP("INTERNSHIP"), 
	OTHERS("OTHERS"), 
	GIAN("GIAN"),
	STAY_EXTENSION("STAY-EXTENSION"),
	SASTHRA("SASTHRA"),
	PROJECT_STAFF("projectStaff"),
	MS_PHD("MsPhD"),
	VALIDATOR("Validator"),
	CCW("CCW");
	private final String value;
	CategoryEnum(String value) {
        this.value = value;
    }
}
