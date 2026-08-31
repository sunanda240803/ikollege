package com.iitm.hosteldine.constant;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

@Component
public class DateUtility {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(Constants.FRONTEND_DATE_FORMAT);
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT, Locale.ENGLISH);
    public static final DateTimeFormatter DATE_TIME_FORMATTER_BACKEND = DateTimeFormatter.ofPattern(Constants.BACKEND_DATE_FORMAT);
    private static final DateTimeFormatter PAYMENY_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(Constants.PAYMENT_DATE_TIME_FORMAT);
    public static final DateTimeFormatter PAYMENY_DATE_TIME_FORMATTER2 =
            new DateTimeFormatterBuilder().appendPattern(Constants.BACKEND_DATE_MON_YEAR_TIME_FORMAT)
                    .appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, true)
                    .toFormatter();

    public static LocalDateTime getNowTimeInstant() {
        return LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
    }

    public static LocalDateTime getStartOfTodayInstant() {
        return LocalDate.now().atStartOfDay();
    }

    public static LocalDateTime getEndOfTodayInstant() {
        return LocalDate.now().plusDays(1).atStartOfDay();
    }

    public static LocalDate getNowDate() {
        return LocalDate.now();
    }

    public static String formatDate(Object date, DateTimeFormatter format) {
        if (date == null) {
            return null;
        }
        try {
            if (date instanceof Date) {
                return SIMPLE_DATE_FORMAT.format(date);
            } else if (date instanceof LocalDate) {
                return ((LocalDate) date).format(DATE_TIME_FORMATTER);
            } else if (date instanceof LocalDateTime) {
                return ((LocalDateTime) date).format(DATE_TIME_FORMATTER);
            } else if (date instanceof Instant) {
                LocalDateTime localDateTime = LocalDateTime.ofInstant((Instant) date, ZoneId.systemDefault());
                return localDateTime.format(DATE_TIME_FORMATTER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static LocalDateTime toLocalDateTime(Object obj) {
        ZoneId zoneId=ZoneId.systemDefault();
        if (obj == null) {
            return null;
        }
        else if (obj instanceof LocalDateTime) {
            return (LocalDateTime) obj;
        }
        else if (obj instanceof Timestamp) {
            return ((Timestamp) obj).toInstant()
                    .atZone(zoneId)
                    .toLocalDateTime();
        }
        else if (obj instanceof LocalDate) {
            return ((LocalDate) obj).atStartOfDay();
        }
        else if (obj instanceof Date) {
            return ((Date) obj).toInstant()
                    .atZone(zoneId)
                    .toLocalDateTime();
        }

        throw new IllegalArgumentException("Unsupported date type: " + obj.getClass());
    }


    public static String formatDate(Object date) {
        return formatDate(date, DATE_TIME_FORMATTER);
    }

    public static LocalDate currentDateOnly() {
        return LocalDate.parse(getNowDate().format(DATE_TIME_FORMATTER), DATE_TIME_FORMATTER); //MMM dd,yyyy
    }

    public static LocalDate stringToLocalDate(String date) {
        String normalized = date
                .replace("\u00A0", " ")  // replace non-breaking space
                .trim()
                .replaceAll("\\s+", " "); // collapse multiple spaces to one

        return LocalDate.parse(normalized, DATE_TIME_FORMATTER); //MMM dd,yyyy
    }

    // Method to convert a string date in the format dd/MM/yyyy HH:mm:ss to LocalDateTime
    public static LocalDateTime parseToLocalDateTime(String dateStr) {
        return Optional.ofNullable(dateStr)
                .map(date -> LocalDateTime.parse(date, PAYMENY_DATE_TIME_FORMATTER))
                .orElse(null);
    }

    // Method to convert a string date in the format yyyy-MM-dd HH:mm:ss.SSS to LocalDateTime
    public static LocalDateTime parseToLocalDateTime2(String dateStr) {
        return Optional.ofNullable(dateStr)
                .map(date -> LocalDateTime.parse(date, PAYMENY_DATE_TIME_FORMATTER2))
                .orElse(null);
    }

    public static String formatDateInd(Date date) {
        return formatDate(date);
    }

    public static String formatDateInd(LocalDate date) {
        return formatDate(date);
    }

    public static String formatDateTime(LocalDateTime date) {
        return formatDate(date);
    }

    public static LocalDate getNextYearDate() {
        return LocalDate.now().plusYears(1);
    }

    public static LocalDate getPreviousDate() {
        return LocalDate.now().minusDays(1);
    }

    public static String threeLetterMonthDateFormat(String date) {
        String theFormattedDate = null;
        try {
            String currentDateFomat = date;
            DateFormat formatter;
            Date dateFormat;
            formatter = new SimpleDateFormat(Constants.BACKEND_DATE_FORMAT);
            dateFormat = (Date) formatter.parse(currentDateFomat);
            java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat(
                    Constants.FRONTEND_DATE_FORMAT);

            theFormattedDate = dateFormatter.format(dateFormat);

        } catch (Exception e) {

        }
        return theFormattedDate;
    }

    public static LocalDate tryParseDate(String dateString) throws Exception {
        String[] dateFormatStrings = {"dd-MM-yy", "dd/MM/yy", "dd-MMM-yy", "dd/MMM/yy", "dd.MM.yy",
                "dd-MM-yyyy", "dd/MM/yyyy", "dd-MMM-yyyy", "dd/MMM/yyyy", "dd.MM.yyyy"};
        java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat(Constants.BACKEND_DATE_FORMAT);
        Date date;
        String dateStr;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -20);
        for (String formatString : dateFormatStrings) {
            try {
                java.text.SimpleDateFormat loopFormatter = new SimpleDateFormat(formatString);
                loopFormatter.set2DigitYearStart(cal.getTime());
                date = loopFormatter.parse(dateString);
                dateStr = dateFormatter.format(date);
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(Constants.BACKEND_DATE_FORMAT);
                return stringToLocalDate(dateStr);

            } catch (Exception e) {
                //  System.out.println("exception");
            }
        }
        return null;
    }

    public static Calendar parseToCalendar(String dateStr, String format) {
        SimpleDateFormat smd = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTimeInMillis(smd.parse(dateStr).getTime());
        } catch (ParseException e) {
            cal = null;
        }
        return cal;
    }

    public static Calendar localDateToCalendar(LocalDate localDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, localDate.getYear());
        calendar.set(Calendar.MONTH, localDate.getMonthValue() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, localDate.getDayOfMonth());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static LocalDate parseToLocalDate(String dateString) throws Exception {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        // Clean the input string (remove any unwanted characters)
        dateString = dateString.replaceAll("[^0-9a-zA-Z]", "-");

        // Define all possible date formats
        String[] dateFormatStrings = {
                "dd-MM-yyyy", "dd/MM/yyyy", "dd-MMM-yyyy", "dd/MMM/yyyy", "dd.MM.yyyy",
                "dd-MM-yy", "dd/MM/yy", "dd-MMM-yy", "dd/MMM/yy", "dd.MM.yy",
                "yyyy-MM-dd", "yyyy/MM/dd", "yyyy.MM.dd"
        };

        // Try parsing with each format
        for (String formatString : dateFormatStrings) {
            try {
                // For two-digit years, use a pivot year of 2000
                if (formatString.contains("yy") && !formatString.contains("yyyy")) {
                    DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                            .appendPattern(formatString)
                            .parseDefaulting(ChronoField.YEAR_OF_ERA, 2000)
                            .toFormatter();
                    return LocalDate.parse(dateString, formatter);
                } else {
                    return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(formatString));
                }
            } catch (DateTimeParseException e) {
                // Continue to next format
            }
        }
        return null;
    }

    public static String formatSqlDateToStringWithFormat(java.sql.Date sqlDate) {
        java.util.Date utilDate = new java.util.Date(sqlDate.getTime());
        return SIMPLE_DATE_FORMAT.format(utilDate);
    }

    public static LocalDate parseSqlDateToLocalDate(java.sql.Date sqlDate) throws Exception {
        java.util.Date utilDate = new java.util.Date(sqlDate.getTime());
        SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(Constants.BACKEND_DATE_FORMAT);
        String formatted = SIMPLE_DATE_FORMAT.format(utilDate);
        return parseToLocalDate(formatted);
    }

    public static LocalDate dateTimeStrtoLocalDate2(String dateTimeStr) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(Constants.BACKEND_DATE_MON_YEAR_TIME_FORMAT);
        return LocalDate.parse(dateTimeStr, inputFormatter);
    }

    public static LocalDate dateTimeStrToLocalDate(String dateTimeStr) {
        // Format 1: Mon Jul 27 15:56:36 GMT 05:30 2026
        try {
            dateTimeStr = dateTimeStr.replace("GMT ", "GMT+");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                    "EEE MMM dd HH:mm:ss 'GMT'XXX yyyy",
                    Locale.ENGLISH);
            return ZonedDateTime.parse(dateTimeStr, formatter).toLocalDate();
        } catch (DateTimeParseException ignored) {
        }

        // Format 2: yyyy-MM-dd HH:mm:ss
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.BACKEND_DATE_MON_YEAR_TIME_FORMAT);
            return LocalDateTime.parse(dateTimeStr, formatter).toLocalDate();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Unsupported date format: " + dateTimeStr, e);
        }
    }

    public static boolean isTomorrowCutoffViolated(LocalDate fromDate, String cutOffTimeStr) {

        try {
            LocalDate today = LocalDate.now();

            // Only check if it's tomorrow's date
            if (fromDate.isEqual(today.plusDays(1))) {
                LocalTime now = LocalTime.now();
                LocalTime cutoff = parseCutoffTime(cutOffTimeStr);
                return now.isAfter(cutoff);
            }
            return false;

        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format. Please use dd-MMM-yyyy");
            return false;
        }
    }

    /**
     * Parse cutoff time string (like "20.30" or "08.00") and return LocalTime.
     */
    public static LocalTime parseCutoffTime(String cutOffTimeStr) {
        String[] parts = cutOffTimeStr.split("\\:");
        int cutOffHour = Integer.parseInt(parts[0]);
        int cutOffMinute = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        return LocalTime.of(cutOffHour, cutOffMinute);
    }

    /**
     * Format LocalTime into hh:mm AM/PM string (e.g., "8:30 PM").
     */
    public static String formatCutoffTime(LocalTime cutoff) {
        int hour = cutoff.getHour() % 12 == 0 ? 12 : cutoff.getHour() % 12;
        String minute = String.format("%02d", cutoff.getMinute());
        String amPm = cutoff.getHour() < 12 ? "AM" : "PM";
        return String.format("%d:%s %s", hour, minute, amPm);
    }
}
