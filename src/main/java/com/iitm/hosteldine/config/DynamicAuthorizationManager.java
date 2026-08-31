package com.iitm.hosteldine.config;

import com.iitm.hosteldine.dto.DashboardWidgetMasterDto;
import com.iitm.hosteldine.dto.MenuListDto;
import com.iitm.hosteldine.dto.MyUserDetails;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.iitm.hosteldine.config.WebSecurityConfig.*;

@Component
@RequiredArgsConstructor
public class DynamicAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private static final String ALL = "/**";

    private final DynamicSecurityService dynamicSecurityService;

    public static String enableRolePrivilege;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        String requestURI = context.getRequest().getRequestURI();
        requestURI = requestURI.replace(context.getRequest().getContextPath(), Strings.EMPTY);

        if (isWhitelisted(requestURI)) {
            return new AuthorizationDecision(true);
        }

        Authentication auth = authentication.get();
        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

//        Optional<? extends GrantedAuthority> isAdmin = auth.getAuthorities().stream().filter(it -> it.getAuthority().endsWith("SoftwareAdmin")).findFirst();
//        if ((enableRolePrivilege != null && enableRolePrivilege.equals("true")) && isAdmin.isPresent()) {
            List<MenuListDto> menuList = dynamicSecurityService.getMenusForRole(auth, null);
            for (MenuListDto url : menuList) {
                if (url != null && !url.getUrlPath().isEmpty() && (requestURI.equals(url.getUrlPath())
                        || requestURI.startsWith(url.getUrlPath() + "/"))) {
                    if (url.getSubMenuOrder() == 0) {
                        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
                        userDetails.setDashboardId(url.getMenuId());
                    }
                    return new AuthorizationDecision(true);
                } else if (url != null && url.getWidgetList() != null && !url.getWidgetList().isEmpty()) {
                    for (DashboardWidgetMasterDto widget : url.getWidgetList()) {
                        if (!widget.getWidgetUrl().isEmpty() && (requestURI.equals(widget.getWidgetUrl())
                                || requestURI.startsWith(widget.getWidgetUrl() + "/"))) {
                            return new AuthorizationDecision(true);
                        }
                    }
                } else if (url != null && url.getSubTab() != null && !url.getSubTab().isEmpty()) {
                    for (MenuListDto widget : url.getSubTab()) {
                        if (!widget.getUrlPath().isEmpty() && (requestURI.equals("/" + widget.getUrlPath())
                                || requestURI.startsWith("/" + widget.getUrlPath() + "/"))) {
                            return new AuthorizationDecision(true);
                        }
                    }
                }
            }
            return new AuthorizationDecision(false);
//        } else {
//            return new AuthorizationDecision(true);
//        }
    }

    private boolean isWhitelisted(String requestURI) {
        String[] whitelist = new String[]{urlHome, urlHome2, "/api/logs" + ALL,"/workFlowApi"+ ALL,"/messTokenApi"+ ALL,
                "/messPosApi"+ ALL,"/onlinePaymentCronJob"+ ALL,
                "/assets/css" + ALL, "/assets/css/*" + ALL, "/sample" + ALL,
                "/assets/img" + ALL, "/assets/js" + ALL, "/assets/js/*" + ALL, "/assets/pdf" + ALL,"/ifsc" + ALL,
                urlLogin, urlArchivePhotos + ALL, urlOtherLogin, urlRegister, urlStudentRegistration + ALL, urlEmailIdExists, urlCaptcha,
                urlError, urlForgotPassword, urlSaveOnlineUser, urlAuthenticate, urlIndex,
                urlFacultyLogin, urlHmOfficeLogin, urlValidateCredentials, urlPublic + ALL, urlFile + ALL, urlConvocation + ALL,
                urlLoginIssueMessReg + ALL};
        for (String pattern : whitelist) {
            if (matchPattern(pattern, requestURI)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchPattern(String pattern, String path) {
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return path.startsWith(prefix);
        } else {
            return pattern.equals(path);
        }
    }

}

