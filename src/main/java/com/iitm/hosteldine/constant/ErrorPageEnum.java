package com.iitm.hosteldine.constant;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ErrorPageEnum {
    BAD_REQUEST("badRequest", "Bad Request", new char[]{'4', '0', '0'}, "Something went wrong! Please try again later.", null, null),

    AUTH_FAILURE("authFailure", null, new char[]{'4', '0', '1'}, null, null, null),
    SESSION_EXPIRED("sessionExpired", "Session expired", new char[]{'4', '0', '1'}, "Please login again to continue", "Login screen.", "url.logout"),
    UNAUTHORIZED("unauthorized", "No Access", new char[]{'4', '0', '1'}, "Please login to access this page", "Login screen.", "url.logout"),

    FORBIDDEN("forbidden", "Page not allowed", new char[]{'4', '0', '3'}, "You are not allowed to access this page", "Home screen.", "url.index"),
    NO_DASHBOARD("noDashboards", "No Dashboards assigned", new char[]{'4', '0', '3'}, "No Dashboards are assigned to your role. Please contact Software Admin.", "", "url.logout"),
    NO_TABS("noTabs", "No Tabs assigned", new char[]{'4', '0', '3'}, "No Tabs are assigned to your role. Please contact Software Admin.", "", "url.logout"),

    NOT_FOUND("notFound", "", new char[]{'4', '0', '4'}, "Page Not found", null, null),

    UI_ERROR("uiError", "Internal Error!", new char[]{'5', '0', '0'}, "Please try again later", null, null),
    UNKNOWN_ERROR("unknownError", "Internal Error!", new char[]{'5', '0', '0'}, "Please try again later", null, null),
    ;

    private final String errorCode;
    private final String title;
    private final String message;
    private final String redirectPageName;
    private final String redirectUrl;
    private final char[] httpCode;

    ErrorPageEnum(String errorCode, String title, char[] httpCode, String message, String redirectPageName, String redirectUrl) {
        this.errorCode = errorCode;
        this.title = title;
        this.message = message;
        this.redirectPageName = redirectPageName;
        this.redirectUrl= redirectUrl;
        this.httpCode = httpCode;
    }

    public static ErrorPageEnum getByErrorCode(String errorCode) {
        return Arrays.stream(ErrorPageEnum.values()).filter(v -> v.getErrorCode().equals(errorCode)).findFirst().orElse(ErrorPageEnum.UI_ERROR);
    }
}
