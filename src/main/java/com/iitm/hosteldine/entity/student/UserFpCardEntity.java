package com.iitm.hosteldine.entity.student;

import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ModelConstants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "\"USER_FP_CARD\"", schema = ModelConstants.SCHEMA)
public class UserFpCardEntity {

    @Id
    @Column(name = "user_id", nullable = false, length = 60)
    private String userId;

    @Column(name = "access_card_serial_no", length = 10)
    private String accessCardSerialNo;

    @Column(name = "card_sn", length = 14)
    private String cardsn;

    @Column(name = "pin_no", length = 8)
    private String pinNo;

    @Column(name = "active", length = 10)
    private String active;

    @Column(name = "auto_match", length = 10)
    private String automatch;

    @Column(name = "smart_card", length = 10)
    private String smartcard;

    @Column(name = "finger_print", length = 10)
    private String fingerprint;

    @Column(name = "user_password", length = 10)
    private String userPassword;

    @Column(name = "finger_print_image")
    private byte[] fingerprintImage;

    @Column(name = "card_active_status", length = 1)
    private String cardActiveStatus;

    @Column(name = "reg_access_modified_date")
    private LocalDateTime regAccessModifiedDate;

    @Column(name = "facial", length = 10)
    private String facial;

    @Column(name = "facial_photo")
    private byte[] facialPhoto;

    @Column(name = "facial_template")
    private byte[] facialTemplate;

    @Column(name = "facial_template_type")
    private Integer facialTemplateType;

    public UserFpCardEntity() {

    }

    public UserFpCardEntity(UserFpCardEntity entity) {
        this.accessCardSerialNo = entity.getAccessCardSerialNo();
        this.cardsn = entity.getCardsn();
        this.pinNo = entity.getPinNo();
        this.active = entity.getActive();
        this.automatch = entity.getAutomatch();
        this.smartcard = entity.getSmartcard();
        this.fingerprint = entity.getFingerprint();
        this.userPassword = entity.getUserPassword();
        this.fingerprintImage = entity.getFingerprintImage();
        this.cardActiveStatus = entity.getCardActiveStatus();
        this.regAccessModifiedDate = entity.getRegAccessModifiedDate();
        this.facial = entity.getFacial();
        this.facialPhoto = entity.getFacialPhoto();
        this.facialTemplate = entity.getFacialTemplate();
        this.facialTemplateType = entity.getFacialTemplateType();
    }

    public void onUpdate() {
		setRegAccessModifiedDate(DateUtility.getNowTimeInstant());
	}
}
