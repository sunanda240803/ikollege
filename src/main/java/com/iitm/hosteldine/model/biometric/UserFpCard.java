package com.iitm.hosteldine.model.biometric;

import com.iitm.hosteldine.constant.ModelConstants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Calendar;

@Entity
@Getter
@Setter
@Table(name = "\"USER_FP_CARD\"", schema = ModelConstants.SCHEMA)
public class UserFpCard {
    @Id
    @Column(name = "user_id")
    private String userId;
    @Column(name = "access_card_serial_no")
    private String accessCardSerialNo;
    @Column(name = "card_sn")
    private String cardSn;
    @Column(name = "pin_no")
    private String pinNo;
    @Column(name = "active")
    private String active;
    @Column(name = "auto_match")
    private String autoMatch;
    @Column(name = "smart_card")
    private String smartCard;
    @Column(name = "finger_print")
    private String fingerprint;
    @Column(name = "user_password")
    private String userPassword;
    @Column(name = "finger_print_image")
    private byte[] fingerprintImage;
    @Column(name = "card_active_status")
    private String cardActiveStatus;
    @Column(name = "school_id")
    private Integer schoolId;
    @Column(name = "reg_access_modified_date")
    private Timestamp regAccessModifiedDate;
    @Column(name = "facial")
    private String facial;
    @Column(name = "facial_photo")
    private byte[] facialPhoto;
    @Column(name = "facial_template")
    private byte[] facialTemplate;
    @Column(name = "facial_template_type")
    private Integer facialTemplateType;

    @Transient
    private Calendar expiryDate;
}
