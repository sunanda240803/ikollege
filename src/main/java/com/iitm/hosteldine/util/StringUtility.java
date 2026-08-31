package com.iitm.hosteldine.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class StringUtility {
    public String getNonNullValue(Long value) {
        return value != null ? String.valueOf(value) : "";
    }

    public String getNonNullValue(String value) {
        return value == null ? "" : value;
    }

    public String getNonNullValue(Integer value) {
        return value != null ? String.valueOf(value) : "";
    }

    public static String getNullIfEmpty(String value) {
        return StringUtils.isEmpty(value) ? null : value;
    }
}
