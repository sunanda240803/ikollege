package com.iitm.hosteldine.constant.dean;

import lombok.Getter;

@Getter
public enum AssetCategory {
	
	MINOR_REPAIR("Minor Repair"),
	MAJOR_REPAIR("Major Repair"),
	REPLACEMENT("Replacement"),
	PAINTING_PARTIALLY("Partial"),
	PAINTING_FULLY("Full"),
	BAD_CONDITION("badCondition"),
	GOOD_CONDITION("goodCondition");
	
	private final String value;
    
	AssetCategory(String value) {
        this.value = value;
    }
	
	public String getValue() {
        return value;
    }
}
