package com.iitm.hosteldine.constant.mess;

public enum MessOption {
    BOTH("Accommodation and Mess both"),
    ACCOMODATION("Accommodation Only"),
    MESS("Mess Only");

    private final String value;

    MessOption(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MessOption fromDbValue(String dbValue) {
        if (dbValue == null) {
            return null;
        }
        switch (dbValue.toLowerCase()) {
            case "both":
                return BOTH;
            case "accommodation":
                return ACCOMODATION;
            case "mess":
                return MESS;
            default:
                throw new IllegalArgumentException("Invalid MessOption value: " + dbValue);
        }
    }
}
