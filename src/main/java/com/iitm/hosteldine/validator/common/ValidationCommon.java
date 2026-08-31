package com.iitm.hosteldine.validator.common;

import static com.iitm.hosteldine.validator.common.ValidationConstants.EMAIL_PATTERN;

import java.time.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.Locale;

import org.apache.logging.log4j.util.Strings;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;

import com.iitm.hosteldine.constant.Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidationCommon {
	
	static DateTimeFormatter inputFormatter = new DateTimeFormatterBuilder()
		    .appendPattern(Constants.BACKEND_DATE_MON_YEAR_TIME_FORMAT)
		    .optionalStart()
		    .appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, true)
		    .optionalEnd()
		    .toFormatter();
	
    public static boolean isValidEmailFormat(String email) {
        return email != null && email.matches(EMAIL_PATTERN);
    }
    
    public static String trimString(String value) {
        return value != null ? value.trim() : null;
    }
    
    public static boolean isFutureDate(LocalDate date) {
        return date!=null && date.isAfter(LocalDate.now()); //if its a future date, it will return true
    }
    
	public static LocalDate toLocalDateOrNull(Object obj) {
		try {
			return (obj != null && obj instanceof String && !obj.toString().trim().isEmpty())
					? LocalDate.parse(obj.toString().trim())
					: null;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String formatDate(Object dateObj, DateTimeFormatter formatter) {
	    return (dateObj instanceof Date) 
	        ? Instant.ofEpochMilli(((Date) dateObj).getTime())
	                 .atZone(ZoneId.systemDefault())
	                 .toLocalDate()
	                 .format(formatter)
	        : null;
	}

	public static String toStringOrNull(Object obj) {
		return (obj != null && obj instanceof String && !obj.toString().trim().isEmpty()) ? obj.toString().trim()
				: null;
	}

	public static Integer toIntegerOrZero(Object obj) {
	    return (obj instanceof String && !obj.toString().trim().isEmpty()) 
	            ? Integer.valueOf(obj.toString().trim()) 
	            : 0;
	}
	
	public static boolean isValid(String value) {
		return value != null && !value.trim().isEmpty();
	}
	
	public static String toString(Object obj) {
		return (obj != null && obj instanceof String && !obj.toString().trim().isEmpty()) ? obj.toString().trim()
				: Strings.EMPTY;
	}
	
	public static Long toLongOrZero(Object obj) {
	    return (obj != null && (obj instanceof Long || !obj.toString().trim().isEmpty())) 
	            ? Long.valueOf(obj.toString().trim()) 
	            : 0;
	}
	
	public static String formatDateString(Object dateObj, DateTimeFormatter formatter) {
	    try {
	        if (dateObj instanceof String && !dateObj.toString().trim().isEmpty()) {
	            return LocalDate.parse(dateObj.toString().trim()).format(formatter);
	        }
	    } catch (Exception e) {
	        log.error("Error parsing date: " + e.getMessage());
	    }
	    return Strings.EMPTY;
	}
	
	public static String getStringValueOrHyphen(Object obj) {
		return obj != null && !String.valueOf(obj).trim().isEmpty() && String.valueOf(obj) != null ? String.valueOf(obj) : Constants.HYPHEN;
	}
	
	public static String getReportNonNullValue(Object obj) {
		return (obj != null) ? String.valueOf(obj) : Strings.EMPTY;
	}

	public static Double getSafeDouble(Object obj) {
		try {
			return (obj != null && !obj.toString().trim().isEmpty()) ? Double.valueOf(obj.toString()) : 0.00;
		} catch (NumberFormatException e) {
			return 0.00;
		}
	}
	
	public static String formatTimestampDateString(Object dateObj, DateTimeFormatter formatter) {
		try {
			return dateObj != null
			        ? LocalDateTime.parse((String) dateObj, inputFormatter)
			            .format(formatter)
			        : Strings.EMPTY;
	    } catch (Exception e) {
	        log.error("Error parsing date: " + e.getMessage());
	    }
		return Strings.EMPTY;
	}
	
	public static void validatePhoneNumber(String phoneNumber, BindingResult result, MessageSource messageSource) {
		if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
			result.rejectValue("phoneNumber", "phoneNumber.invalid", messageSource.getMessage("message.validation.phone.number.required", null, Locale.getDefault()));
		} else if (!phoneNumber.matches(ValidationConstants.PHONE_NUMBER_PATTERN)) {
			result.rejectValue("phoneNumber", "phoneNumber.format", messageSource.getMessage("message.validation.phone.number.valid", null, Locale.getDefault()));
		}
	}
}
