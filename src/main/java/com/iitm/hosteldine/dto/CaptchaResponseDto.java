package com.iitm.hosteldine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CaptchaResponseDto {
    private int code;
    private String message;
    private String token;
    private byte[] image;

}
