package com.iitm.hosteldine.form.biometric;

import com.iitm.hosteldine.model.biometric.MessMaster;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Slf4j
public class FrForm {
    private String rollNo;
    private String serialNumber;
    private String serialNumberHidden;
    private String pullIp;
    private String pushIp;
    private String messId;
    private boolean deleteUser;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
    private boolean messPeriodAsExpiry;
    List<MessMaster> messList;
    List<UserListForm> userList;
}
