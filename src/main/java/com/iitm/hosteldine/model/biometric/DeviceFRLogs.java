package com.iitm.hosteldine.model.biometric;

import com.iitm.hosteldine.constant.ModelConstants;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "device_fr_logs", schema = ModelConstants.SCHEMA)
public class DeviceFRLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "fetch_date")
    Timestamp fetchDate;
    @Column(name = "device_ip")
    String deviceIp;
    @Column(name = "log_id")
    Long logId;
    @Column(name = "user_id")
    String userId;
    @Column(name = "log_timestamp")
    Timestamp logTimestamp;
    @Column(name = "event")
    String event;
    @Column(name = "student_id")
    String studentId;
    @Column(name = "mess_id")
    Long messId;
    @Column(name = "mess_name")
    String messName;

}
