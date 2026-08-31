package com.iitm.hosteldine.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginDto {
    private String userName;
    private String password;
    private String loginType;
}
