package com.iitm.hosteldine.validator.common;

public class ValidationConstants {
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final String ALPHANUMERIC_PATTERN = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]+$";
    public static final String NUMERIC_PATTERN = "\\d+";
    public static final String PAN_NUMBER_PATTERN = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$";
    // Regex: Up to 10 digits before decimal, optional one decimal, and up to 2 digits after
    public static final String AMOUNT_PATTERN = "^\\d{1,10}(\\.\\d{1,2})?$";
    public static final String PHONE_NUMBER_PATTERN = "^[0-9]{10}$";
    public static final String[] VALIDATING_AUTHORITY_EMAIL_DOMAINS = { "@iitm.ac.in", "@smail.iitm.ac.in", "@zmail.iitm.ac.in" };
}
