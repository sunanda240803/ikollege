package com.iitm.hosteldine.dto.student;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserFpCardDto {

	private String userId;
    private String accessCardSerialNo;
    private String cardsn;
    private String currentPin;
    private String pinNo;
    private String retypePinNo;
    private String active;
    private String automatch;
    private String smartcard;
    private String fingerprint;
    private String userPassword;
    private byte[] fingerprintImage;
    private String cardActiveStatus;
    private LocalDateTime regAccessModifiedDate;
    private String facial;
    private byte[] facialPhoto;
    private byte[] facialTemplate;
    private Integer facialTemplateType;
	
}
