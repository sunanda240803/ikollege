package com.iitm.hosteldine.form.biometric;

import lombok.Data;

@Data
public class LoginForm {
	private String userName;
	private String password;
	private String userType;
	private String userParam;
}
