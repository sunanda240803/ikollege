package com.iitm.hosteldine.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserManagementDto extends ResetPasswordDto{
    private UserManagementIdDto id;
    private Integer roleId;
    private Integer secondaryRoleId;
    private Integer noFailedAttempts;
    private String isLockable;
    private String email;
    private LocalDateTime passwordModifiedTime;
    private LocalDateTime wrongPwdTime;
    private LocalDateTime lastLoginTime;
    private String accountType;
    private String activeFlag;
    private Long employeeId;
    private String ipAddress;
    private String authenticationServer;
    private String cardPin;
    
    public String getUserName() {
        return getId().getUsername();
    }
}
