package com.iitm.hosteldine.model.hostel;

import com.iitm.hosteldine.constant.ModelConstants;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "\"HOSTEL_BIOMETRIC_LOGS\"", schema = ModelConstants.SCHEMA)
public class HostelBiometricLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 32)
    @Column(name = "userid", length = 32)
    private String userid;

    @Size(max = 30)
    @Column(name = "studentid", length = 30)
    private String studentid;

    @Size(max = 64)
    @Column(name = "access_method", length = 64)
    private String accessMethod;

    @Size(max = 32)
    @Column(name = "terminal_ip", length = 32)
    private String terminalIp;

    @Column(name = "swipe_time")
    private LocalTime swipeTime;

    @Column(name = "swipe_date")
    private LocalDate swipeDate;

    @Size(max = 32)
    @Column(name = "rf_id", length = 32)
    private String rfId;

    @Size(max = 15)
    @Column(name = "terminal_serial_no", length = 15)
    private String terminalSerialNo;

    @Column(name = "local_timestamp")
    private LocalDateTime localTimestamp;

    @Column(name = "login_type", length = Integer.MAX_VALUE)
    private String loginType;

}