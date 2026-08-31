package com.iitm.hosteldine.model.mess;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"MESS_TERMINAL\"", schema = ModelConstants.SCHEMA)
public class MessTerminalEntity extends CommonEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "terminal_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mess_master_id", referencedColumnName = "mess_master_id", nullable = false)
    private MessMasterEntity messMaster;

    @Column(name = "terminal_ip", nullable = false, length = 64)
    private String terminalIp;

    @Column(name = "terminal_mac_id", nullable = false, length = 64)
    private String terminalMacId;

    @Column(name = "terminal_description", length = 128)
    private String terminalDescription;

    @Column(name = "issued_by", length = 32)
    private String issuedBy;

    @Column(name = "username", length = 10)
    private String userName;

    @Column(name = "password", length = 10)
    private String password;

}
