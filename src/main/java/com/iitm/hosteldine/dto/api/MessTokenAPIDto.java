package com.iitm.hosteldine.dto.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessTokenAPIDto {
    private String status;
    private String userName;
    private String messName;
    private Long messId;
    private String qrNumber;
    private Long qrId;
    private String token;
    private String userType;
    private String password;
    private String macId;
    private String source;
    private Long timeLimit;
    private LocalDateTime generatedAt;
    private LocalDateTime lastUsed;
    private String terminalIp;
    private String cardSerialNo;
    private String pinNumber;
    private Integer cardStatus;
    private String studID;
    private String studentName;
    private Double amount;
    private String billNo;
    private String billingDate;
    private Integer saveStatus;
    private String voucherNo;
}
