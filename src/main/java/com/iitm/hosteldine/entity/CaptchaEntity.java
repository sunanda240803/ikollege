package com.iitm.hosteldine.entity;

import com.iitm.hosteldine.constant.ModelConstants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "\"CAPTCHA_TOKEN\"", schema = ModelConstants.SCHEMA)
public class CaptchaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", length = 20)
	private long id;
	
	@Column(name = "token")
	private String token;
	
	@Column(name = "captcha_code")
	private String captchaCode;
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;
}
