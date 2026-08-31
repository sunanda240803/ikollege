package com.iitm.hosteldine.exception;

import com.iitm.hosteldine.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.thymeleaf.exceptions.TemplateInputException;

import static com.iitm.hosteldine.constant.ErrorPageEnum.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    public static final String PAGE_NOT_ALLOWED_PARAM = "?error=" + UNAUTHORIZED.getErrorCode();
    public static final String BAD_REQUEST_PARAM = "?error=" + BAD_REQUEST.getErrorCode();
    public static final String SESSION_EXPIRED_PARAM = "?error=" + SESSION_EXPIRED.getErrorCode();
    public static final String AUTH_FAILED_PARAM = "?error=" + AUTH_FAILURE.getErrorCode();
    public static final String NO_DASHBOARD_PARAM = "?error=" + NO_DASHBOARD.getErrorCode();
    public static final String UI_ERROR_PARAM = "?error=" + UI_ERROR.getErrorCode();
    public static final String UNKNOWN_ERROR_PARAM = "?error=" + UNKNOWN_ERROR.getErrorCode();
    public static final String NO_TABS_PARAM = "?error=" + NO_TABS.getErrorCode();

    @Value("${url.error}")
    private String baseUrl;

    @ExceptionHandler({Exception.class})
    public String handleOtherException(Exception exception) {
        log.error("Unknown Error: {}", exception.getMessage());
        exception.printStackTrace();
        return Constants.REDIRECT + baseUrl + UNKNOWN_ERROR_PARAM;
    }

    @ExceptionHandler({TemplateInputException.class})
    public String handleUIException(TemplateInputException exception) {
        log.error("UI Error: {}", exception.getMessage());
        exception.printStackTrace();
        return Constants.REDIRECT + baseUrl + UI_ERROR_PARAM;
    }

    @ExceptionHandler({RecordNotExistsException.class})
    public String handleRecordNotExistsException(RecordNotExistsException exception) {
        log.error("Error: {}", exception.getMessage());
        return Constants.REDIRECT + baseUrl + BAD_REQUEST_PARAM;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleAccessDenied(NoResourceFoundException ex) {
        log.error("Resource not found: {}", ex.getResourcePath());
        return Constants.REDIRECT + baseUrl + PAGE_NOT_ALLOWED_PARAM;
    }

    @ExceptionHandler({UserRoleNotMappedException.class})
    public String handleUserRoleNotMapped(UserRoleNotMappedException exception) {
        log.error("Role not mapped: ", exception);
        return Constants.REDIRECT + baseUrl + PAGE_NOT_ALLOWED_PARAM;
    }
}
