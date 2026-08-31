package com.iitm.hosteldine.model.hostel;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"HOSTEL_BIOMETRIC_TERMINAL\"", schema = ModelConstants.SCHEMA)
public class HostelBiometricTerminalEntity extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "terminal_id")
    private Long terminalId;

    @Column(name = "terminal_ip", nullable = false, length = 64)
    private String terminalIp;

    @Column(name = "terminal_location", nullable = false, length = 64)
    private String terminalLocation;

    @Column(name = "terminal_mac_id", nullable = false, length = 64)
    private String terminalMacId;

    @Column(name = "terminal_description", length = 128)
    private String terminalDescription;

    @Column(name = "issued_by", length = 32)
    private String issuedBy;

    @Column(name = "username", length = 10)
    private String username;

    @Column(name = "password", length = 10)
    private String password;

    @Column(name = "gender", length = 8)
    private String gender;

}