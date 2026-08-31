package com.iitm.hosteldine.dto.hostel;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link com.iitm.hosteldine.model.hostel.HostelBiometricTerminalEntity}
 */
@Data
@Builder
public class HostelBiometricTerminalDto implements Serializable {
    private Long terminalId;
    private String terminalIp;
    private String terminalLocation;
    private String terminalMacId;
    private String terminalDescription;
    private String issuedBy;
    private String username;
    private String password;
    private String gender;
}