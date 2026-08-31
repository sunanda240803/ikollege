package com.iitm.hosteldine.config;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.exception.GlobalExceptionHandler;
import com.iitm.hosteldine.util.MCrypt;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Value("${url.login}")
    private String urlLogin;
    @Value("${url.faculty.login}")
    private String urlFacultyLogin;
    @Value("${url.hm.office.login}")
    private String urlHmOfficeLogin;
    @Value("${url.other.login}")
    private String urlOtherLogin;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String loginType = null;
        String contextPath = request.getContextPath();

        try {
            loginType = MCrypt.getInstance().decryptToString(request.getParameter("loginType"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String redirectUrl = switch (loginType) {
            case ModelConstants.STUDENT_LOGIN_TYPE -> urlLogin;
            case ModelConstants.OTHER_LOGIN_TYPE -> urlOtherLogin;
            case ModelConstants.HM_OFFICE_LOGIN_TYPE -> urlHmOfficeLogin;
            case ModelConstants.FACULTY_LOGIN_TYPE -> urlFacultyLogin;
            default -> "/";
        };
        response.sendRedirect(contextPath + redirectUrl + GlobalExceptionHandler.AUTH_FAILED_PARAM);
    }
}
