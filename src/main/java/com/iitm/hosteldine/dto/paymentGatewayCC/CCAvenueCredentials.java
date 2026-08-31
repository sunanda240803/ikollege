package com.iitm.hosteldine.dto.paymentGatewayCC;

import lombok.Data;

@Data
public class CCAvenueCredentials {
    private String accessCode;
    private String workingKey;

    public CCAvenueCredentials(String accessCode, String workingKey) {
        this.accessCode = accessCode;
        this.workingKey = workingKey;
    }
}
