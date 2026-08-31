package com.iitm.hosteldine.model.biometric;

import com.iitm.hosteldine.constant.ModelConstants;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "\"MESS_TERMINAL\"", schema = ModelConstants.SCHEMA)
public class MessTerminal {
    @Id
    @Column(name = "terminal_id")
    private Long terminalId;
    @Column(name = "mess_master_id")
    private Long messMasterId;
    @Column(name = "terminal_ip")
    private String terminalIp;
    @Column(name = "terminal_mac_id")
    private String terminalMacId;
    @Column(name = "terminal_description")
    private String terminalDescription;
    @Column(name = "issued_by")
    private String issuedBy;
    @Column(name = "active_flag")
    private String activeStatus;
    @Column(name = "school_id")
    private Integer schoolId;
    @Column(name = "username")
    private String userName;
    @Column(name = "password")
    private String password;

    @Override
    public String toString() {
        return "ip: " + terminalIp;
    }
}
