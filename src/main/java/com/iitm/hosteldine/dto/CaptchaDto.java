package com.iitm.hosteldine.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CaptchaDto {
	private String token;
	private String captchaCode;
}
