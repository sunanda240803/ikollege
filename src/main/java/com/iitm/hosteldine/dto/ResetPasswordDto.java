package com.iitm.hosteldine.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class ResetPasswordDto {
    private String applicantPassword;
    private String confirmPassword;
    private String newPassword;
    private String loginType;
}
