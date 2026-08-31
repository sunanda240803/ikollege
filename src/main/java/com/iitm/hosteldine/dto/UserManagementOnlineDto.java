package com.iitm.hosteldine.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@RequiredArgsConstructor
public class UserManagementOnlineDto extends ResetPasswordDto{
    private String applicantName;
    private String applicantEmail;
    private String userName;
    private LocalDateTime lastLoginTime;
    private Long noOfFailedAttempts;
    private String userEmailId;
    private String activeStatus;
}
