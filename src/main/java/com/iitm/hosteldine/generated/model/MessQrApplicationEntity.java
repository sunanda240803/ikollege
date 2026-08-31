package com.iitm.hosteldine.generated.model;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "\"MESS_QR_APPLICATION\"", schema = ModelConstants.SCHEMA)
public class MessQrApplicationEntity extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qr_id")
    private Long qrId;

    @Column(name = "student_id", length = 16)
    private String studentId;

    @Column(name = "qr_number", length = 32)
    private String qrNumber;

    @Column(name = "mess_session", length = 32)
    private String messSession;

    @Column(name = "mess_id")
    private Long messId;

    @Column(name = "qr_usage_status", length = 32)
    private String qrUsageStatus;

    @Column(name = "qr_usage_date")
    private LocalDate qrUsageDate;

    @Column(name = "qr_usage_time")
    private LocalDateTime qrUsageTime;

}

