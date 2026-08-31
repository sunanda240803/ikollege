package com.iitm.hosteldine.util;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.SimsConfigDataService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.time.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class Utility {

    @Value("${date.format}")
    private String dateFormat;

    public static String durationInString(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - (hours * 60);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - ((hours * 60 * 60) + (minutes * 60));
        return (hours > 0 ? (hours + "H ") : "") + (minutes > 0 ? (minutes + "M ") : "") + (seconds > 0 ? (seconds + "S ") : "");
    }

    public LocalDate convertToLocalDate(Object obj) {
        return switch (obj) {
            case null -> null;
            case LocalDate localDate -> localDate;
            case String str -> LocalDate.parse(str);
            case java.sql.Date sqlDate -> sqlDate.toLocalDate();
            case java.util.Date utilDate -> utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            case java.time.Instant instant -> instant.atZone(ZoneId.systemDefault()).toLocalDate();
            case java.time.LocalDateTime dateTime -> dateTime.toLocalDate();
            default -> throw new IllegalArgumentException("Cannot convert to LocalDate: " + obj);
        };
    }

    public double parseDouble(Object obj) {
        return Optional.ofNullable(obj)
                .map(data -> String.valueOf(data).trim())
                .map(data -> data.isEmpty() ? "0.0" : data)
                .map(Double::parseDouble)
                .orElse(0.0);
    }

    public long parseLong(Object obj) {
        return Optional.ofNullable(obj)
                .map(data -> String.valueOf(data).trim())
                .map(data -> data.isEmpty() ? "0" : data)
                .map(Long::parseLong)
                .orElse(0L);
    }

    public int parseInt(Object obj) {
        return Optional.ofNullable(obj)
                .map(data -> String.valueOf(data).trim())
                .map(data -> data.isEmpty() ? "0" : data)
                .map(Integer::parseInt)
                .orElse(0);
    }

    public String parseString(Object obj) {
        return Optional.ofNullable(obj)
                .map(String::valueOf)
                .orElse(Strings.EMPTY);
    }

    public Boolean parseBoolean(Object obj) {
        return Optional.ofNullable(obj)
                .map(o -> Boolean.parseBoolean(String.valueOf(obj)))
                .orElse(false);

    }

    public String formatCommaSeperatedCurrency(Double amount) {
        DecimalFormat indianFormatter = new DecimalFormat(Constants.DECIMAL_FORMAT);
        indianFormatter.setCurrency(Currency.getInstance(new Locale(Constants.LANG, Constants.COUNTRY)));
        return (String.format(ModelConstants.TWO_DECIMAL_POINT, amount)
                .split(ModelConstants.AMOUNT_STRING_SPLITTER)[0].length() <= 5)
                ? indianFormatter.format(amount != null ? amount : 0)
                : formatToIndianRupees(amount != null ? amount : 0);
    }

    public String formatToIndianRupees(double amount) {
        String amountStr = String.format(ModelConstants.TWO_DECIMAL_POINT, amount);
        String[] parts = amountStr.split(ModelConstants.AMOUNT_STRING_SPLITTER);
        String integerPart = parts[0];
        String decimalPart = ModelConstants.DOT + parts[1];
        String formattedInteger = IntStream.range(0, integerPart.length())
                .mapToObj(integerPart::charAt)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            StringBuilder sb = new StringBuilder();
                            int count = 0;
                            for (int i = list.size() - 1; i >= 0; i--) {
                                sb.insert(0, list.get(i));
                                count++;
                                if (count == 3 && i != 0) {
                                    sb.insert(0, ModelConstants.COMMA);
                                    count = 0;
                                } else if (count == 2 && list.size() - i > 3 && i != 0) {
                                    sb.insert(0, ModelConstants.COMMA);
                                    count = 0;
                                }
                            }
                            return sb.toString();
                        }
                ));
        return formattedInteger + decimalPart;
    }

    public String dateFormatter(LocalDate localDate) {
        return dateFormatter(localDate, dateFormat);
    }

    public String dateFormatter(LocalDate localDate, String format) {
        if (localDate == null) {
            return Strings.EMPTY;
        }
        return localDate.format(DateTimeFormatter.ofPattern(format));
    }

    public String dateFormatterLocalDateTime(LocalDateTime localDateTime, String format) {
        if (localDateTime == null) {
            return Strings.EMPTY;
        }
        return localDateTime.format(DateTimeFormatter.ofPattern(format));
    }

    public String dateFormatter(Instant instant) {
        return dateFormatter(convertToLocalDate(instant));
    }

//    public String dateFormatter(Instant instant, String format) {
//        return dateFormatter(convertToLocalDateTime(instant), format);
//    }

    public static boolean validateRegexPattern(String regex, String key) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(key);
        return matcher.matches();
    }

    public static String capitalizeEachWordStream(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Arrays.stream(str.split("\\s+"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "));
    }

    public static String getDomainUrl(HttpServletRequest request) {
        String domainName = request.getServerName();
        int port = request.getServerPort();
        String contextPath = request.getContextPath();
        if (domainName.equalsIgnoreCase("localhost") && port > 0) {
            return Constants.HTTP + domainName + ":" + port + contextPath;
        }
        return Constants.HTTPS + domainName + contextPath;
    }


    public static String accommodationRequestKey(String status, Long requestId, Long candidateId, Long stayId) throws Exception {
        String encryptKey = status + Constants.BACKTICK + candidateId + Constants.BACKTICK + requestId + Constants.BACKTICK
                + stayId + Constants.BACKTICK + getCurrentTimeStamp();
        return MCrypt.getInstance().encryptToText(encryptKey);
    }

    public static String getCurrentTimeStamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static Boolean checkRequestType(String encryptedKey) throws Exception {
        String[] split = MCrypt.getInstance().decryptToString(encryptedKey).split(Constants.BACKTICK);
        long currentTimeMillis = System.currentTimeMillis();
        if (split.length == 5 && split[4].length() == String.valueOf(currentTimeMillis).length()) {
            long requestTime = Long.parseLong(split[4]);

            long differenceInMillis = Math.abs(currentTimeMillis - requestTime);
            long oneHourInMillis = Long.parseLong(SimsConfigDataService.TIME_DIFF) * 60 * 60 * 1000;

            return differenceInMillis <= oneHourInMillis;
        }

        return false;
    }

    public static Boolean checkRequestType(String encryptedKey, Integer splitLength, Integer sysTimeIndex)
            throws Exception {
        String[] split = MCrypt.getInstance().decryptToString(encryptedKey).split(Constants.BACKTICK);
        long currentTimeMillis = System.currentTimeMillis();
        if (split.length == splitLength && split[sysTimeIndex].length() == String.valueOf(currentTimeMillis).length()) {
            long requestTime = Long.parseLong(split[sysTimeIndex]);

            long differenceInMillis = Math.abs(currentTimeMillis - requestTime);
            long oneHourInMillis = Long.parseLong(SimsConfigDataService.TIME_DIFF) * 60 * 60 * 1000;

            return differenceInMillis <= oneHourInMillis;
        }
        return false;
    }

    public static <T extends Number> T numToNullIfZero(T value) {
        return switch (value) {
            case null -> null;
            case Long l when value.longValue() == 0L -> null;
            case Integer i when value.intValue() == 0 -> null;
            case Double i when value.doubleValue() == 0 -> null;
            default -> value;
        };
    }

    public LocalDateTime convertToLocalDateTime(Object dateObject) {
        return switch (dateObject) {
            case null -> null;
            case String dateString -> {
                DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                        .appendPattern(Constants.BACKEND_DATE_FORMAT + " " + Constants.TIME_FORMAT_SS)
                        .optionalStart()
                        .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true)
                        .optionalEnd()
                        .toFormatter();
                yield LocalDateTime.parse(dateString, formatter);
            }
            case java.time.Instant instant -> LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            case java.time.LocalDateTime date -> (LocalDateTime) date;
            default -> throw new IllegalArgumentException("Cannot convert to LocalDateTime: " + dateObject);
        };
    }

    public Object getFormAdditionalParam(PaginationForm form, String key) {
        return Optional.ofNullable(form.getAdditionalParam().get(key))
                .filter(val -> !val.toString().isEmpty())
                .orElse(null);
    }

    public static String[] decryptData(String data) throws Exception {
        return MCrypt.getInstance().decryptToString(data).split(Constants.BACKTICK);
    }

    public static String getValueOrDefault(String[] array, int index, String defaultValue) {
        return (array != null && array.length > index && array[index] != null) ? array[index] : defaultValue;
    }

    public static Long getLongValueOrDefault(String[] array, int index, Long defaultValue) {
        try {
            return (array != null && array.length > index && array[index] != null) ? Long.parseLong(array[index]) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static String encryptAccommodationRequestUrl(String studentId, Long requestId, String status) throws Exception {
        String encryptKey = null;
        if (status != null && !status.isEmpty()) {
            encryptKey = studentId + Constants.BACKTICK + requestId + Constants.BACKTICK + status + Constants.BACKTICK + Utility.getCurrentTimeStamp();
        } else {
            encryptKey = studentId + Constants.BACKTICK + requestId + Constants.BACKTICK + Utility.getCurrentTimeStamp();
        }
        return MCrypt.getInstance().encryptToText(encryptKey);
    }

    public static String encryptWorkflowUrl(String studentId, Long requestId, String status, Long workflowId) throws Exception {
        String encryptKey = null;
        if (status != null && !status.isEmpty()) {
            encryptKey = studentId + Constants.BACKTICK + requestId + Constants.BACKTICK + status + Constants.BACKTICK + workflowId + Constants.BACKTICK + Utility.getCurrentTimeStamp();
        } else {
            encryptKey = studentId + Constants.BACKTICK + requestId + Constants.BACKTICK + Utility.getCurrentTimeStamp();
        }
        return MCrypt.getInstance().encryptToText(encryptKey);
    }

    public static String getGender(Object data) {
        if (data != null) {
            if (data.toString().equalsIgnoreCase(Constants.MALE)) {
                return Constants.MALE_FULL_FORM;
            } else if (data.toString().equalsIgnoreCase(Constants.FEMALE)) {
                return Constants.FEMALE_FULL_FORM;
            }
        }
        return Strings.EMPTY;
    }

    public static Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public String convertDateStr(String dateStr, String inputFormat, String outputFormat) {
        if (dateStr == null || dateStr.isEmpty())
            return Strings.EMPTY;
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(inputFormat);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormat);
            LocalDateTime dateTime = LocalDateTime.parse(dateStr, inputFormatter);
            return dateTime.format(outputFormatter);
        } catch (Exception e) {
            return Strings.EMPTY;
        }
    }

    public String convertDateToString(Date date, String format) {
        if (date == null)
            return Strings.EMPTY;
        try {
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(format, Locale.ENGLISH);
            if (date instanceof java.sql.Date sqlDate) {
                return sqlDate.toLocalDate().format(outputFormatter);
            } else {
                LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                return localDate.format(outputFormatter);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Strings.EMPTY;
        }
    }

    public String convertTimeStamp(String timeStamp){
        if(Objects.isNull(timeStamp) || timeStamp.isEmpty()){
            return Strings.EMPTY;
        }
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yy  HH:mm", Locale.ENGLISH);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMM, yyyy HH:mm", Locale.ENGLISH);
        LocalDateTime dateTime = LocalDateTime.parse(timeStamp, inputFormatter);
        return dateTime.format(outputFormatter);
    }

    public static String formatAmount(Double amount) {
        if (amount == null) return "0";
        return amount % 1 == 0
                ? String.valueOf(amount.longValue())
                : String.format("%.2f", amount);
    }

    public static ResponseEntity<Resource> prepareDownloadFile(Resource resource) {
        return prepareDownloadFile(resource, resource.getFilename());
     }

    public static ResponseEntity<Resource> prepareDownloadFile(Resource resource, String fileName) {
        return  prepareDownloadFileHeader(fileName).body(resource);
    }

    public static ResponseEntity<Resource> prepareDownloadFile(ByteArrayResource resource) {
        return prepareDownloadFile(resource, resource.getFilename());
     }

    public static ResponseEntity<Resource> prepareDownloadFile(ByteArrayResource resource, String fileName) {
        return  prepareDownloadFileHeader(fileName).body(resource);
    }

    private static ResponseEntity.BodyBuilder prepareDownloadFileHeader(String fileName) {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName+"\"")
                .contentType(MediaType.APPLICATION_PDF);
    }
}
