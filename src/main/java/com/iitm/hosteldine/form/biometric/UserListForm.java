package com.iitm.hosteldine.form.biometric;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserListForm {
    private String userId;
    private String firstName;
    private String lastName;
    private String accessSerialNo;
    private String messName;
    private boolean hasFingerprint;
    private boolean hasCardSn;
    private boolean hasFacial;
    private byte[] facialPhoto;
    private String facialPhotoSrc;
    private String cardSn;
    private String pinNo;
    private String cardActiveStatus;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
