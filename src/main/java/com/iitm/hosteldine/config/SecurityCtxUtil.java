package com.iitm.hosteldine.config;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.iitm.hosteldine.dto.warden.WardenInfoDto;
import com.iitm.hosteldine.service.warden.WardenInfoService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.MyUserDetails;
import com.iitm.hosteldine.dto.StudentDetails;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.util.RoleEnum;

@Component
public class SecurityCtxUtil {
    public static final String ANONYMOUS_USER = "anonymousUser";
    private static SimsConfigDataService simsConfigDataService;
    private static WardenInfoService wardenInfoService;

    public SecurityCtxUtil(SimsConfigDataService simsConfigDataService, WardenInfoService wardenInfoService) {
        SecurityCtxUtil.simsConfigDataService = simsConfigDataService;
        SecurityCtxUtil.wardenInfoService = wardenInfoService;
    }

    public static String userId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            Object principal = auth.getPrincipal();
            if (principal instanceof MyUserDetails) {
                MyUserDetails principalObj = (MyUserDetails) principal;
                return String.valueOf(principalObj.getUserId());
            } else if (principal instanceof String) {
                return (String) principal;
            }
        }
        return Strings.EMPTY; // or return a default value if appropriate
    }

    public static String userName() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(MyUserDetails.class::isInstance)
                .map(MyUserDetails.class::cast)
                .map(userDetails -> 
                	ModelConstants.STUDENT.equals(userDetails.getAccountType()) 
	                ? userDetails.getUserName().toUpperCase() 
	                : userDetails.getUserName()
                )
                .orElse(null);
    }

    public static String studentFullName() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(MyUserDetails.class::isInstance)
                .map(MyUserDetails.class::cast)
                .map(MyUserDetails::getStudentDetails)
                .map(StudentDetails::getStudentFullName)
                .orElse(null);
    }

    public static String hostelName() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(MyUserDetails.class::isInstance)
                .map(MyUserDetails.class::cast)
                .map(MyUserDetails::getStudentDetails)
                .map(StudentDetails::getHostelName)
                .orElse(null);
    }

    public static String messName() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(MyUserDetails.class::isInstance)
                .map(MyUserDetails.class::cast)
                .map(MyUserDetails::getStudentDetails)
                .map(StudentDetails::getMess)
                .orElse(null);
    }

    public static String roomNumber() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(MyUserDetails.class::isInstance)
                .map(MyUserDetails.class::cast)
                .map(MyUserDetails::getStudentDetails)
                .map(StudentDetails::getRoomNumber)
                .orElse(null);
    }

    public static LocalDateTime sessionStartTime() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(MyUserDetails.class::isInstance)
                .map(MyUserDetails.class::cast)
                .map(MyUserDetails::getStudentDetails)
                .map(StudentDetails::getSessionStartTime)
                .orElse(null);
    }

    public static Long hostelId() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(MyUserDetails.class::isInstance)
                .map(MyUserDetails.class::cast)
                .map(MyUserDetails::getStudentDetails)
                .map(StudentDetails::getHostelId)
                .orElse(0L);
    }

    public static Long roomId() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(MyUserDetails.class::isInstance)
                .map(MyUserDetails.class::cast)
                .map(MyUserDetails::getStudentDetails)
                .map(StudentDetails::getRoomId)
                .orElse(0L);
    }

    public static Long candidateId() {
        return Long.valueOf(
                Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                        .map(Authentication::getPrincipal)
                        .filter(MyUserDetails.class::isInstance)
                        .map(MyUserDetails.class::cast)
                        .map(MyUserDetails::getCandidateId)
                        .orElse(String.valueOf(0L)));
    }

    public static String accountType() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(MyUserDetails.class::isInstance)
                .map(MyUserDetails.class::cast)
                .map(MyUserDetails::getAccountType)
                .orElse(null);
    }

    public static String userRole() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getAuthorities)
                .flatMap(authorities -> authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(role -> role.replaceFirst(Constants.PREFIX_TEXT_ROLE, ModelConstants.EMPTY_STRING)) // Remove "ROLE_"
                        .findFirst())
                .orElse(Constants.ANONYMOUS);
    }

    public static String userSecondaryRole() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getAuthorities)
                .map(authorities -> authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(role -> role.replaceFirst(Constants.PREFIX_TEXT_ROLE, ModelConstants.EMPTY_STRING))
                        .toList()
                )
                .filter(list -> list.size() > 1)
                .map(List::getLast)
                .orElse(Constants.ANONYMOUS);
    }

    public static Long dashboardId() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(MyUserDetails.class::isInstance)
                .map(MyUserDetails.class::cast)
                .map(MyUserDetails::getDashboardId)
                .orElse(0L);
    }

    public static boolean isHostelRole() {
        return userRole() != null && userRole().equalsIgnoreCase(RoleEnum.HOSTEL_CHECK_IN.getValue());
    }

    public static boolean isWardenRole() {
        return userRole() != null && userRole().equalsIgnoreCase(RoleEnum.WARDEN.getValue());
    }    
        
    public static boolean isAdmin() {
        String adminControlRoleName = simsConfigDataService.getSimConfigValue(SimsConfigDataService.ADMIN_CONTROL_ROLE_NAME);
        String[] adminRoles = adminControlRoleName.split(",");

        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
	        .map(Authentication::getPrincipal)
	        .filter(MyUserDetails.class::isInstance)
	        .map(MyUserDetails.class::cast)
	        .map(userDetails -> {
	            String accountType = userDetails.getAccountType();
	            for (String role : adminRoles) {
	                if (role.equalsIgnoreCase(accountType)) {
	                    return true;
	                }
	            }
	            return false;
	        })
	        .orElse(false);
    }

    public static String getProfile() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(MyUserDetails.class::isInstance)
                .map(MyUserDetails.class::cast)
                .map(MyUserDetails::getProfileImageName)
                .orElse(null);
    }

    public static String getProfileName() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(MyUserDetails.class::isInstance)
                .map(MyUserDetails.class::cast)
                .map(MyUserDetails::getProfileName)
                .orElse(null);
    }

    public static Long wardenId() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(MyUserDetails.class::isInstance)
                .map(MyUserDetails.class::cast)
                .map(MyUserDetails::getUserName)
                .flatMap(u -> Optional.ofNullable(wardenInfoService.getWardenDetailsByLDAPUsername(userName())))
                .map(WardenInfoDto::getId)
                .orElse(0L);
    }
}
