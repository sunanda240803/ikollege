package com.iitm.hosteldine.model.biometric;


import com.iitm.hosteldine.constant.ModelConstants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "\"STUDENT_MESS_DETAILS\"", schema = ModelConstants.SCHEMA)
public class StudentMessDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

	@Column(name = "student_id", length = 32)
	private String studentId;

	@Column(name = "mess_id", nullable = false)
	private Long messId;

    @Column(name = "from_date")
    private Date fromDate;

    @Column(name = "to_date")
    private Date toDate;

    @Column(name = "user_ip", length = 64)
    private String userIp;

    @Column(name = "hostel_signed", length = 8)
    private String hostelSigned;

    @Column(name = "mess_signed", length = 8)
    private String messSigned;

    @Column(name = "allotted_sl_no")
    private Integer allottedSlno;

    @Column(name = "change_from_date")
    private LocalDate changeFromDate;

    @Column(name = "change_to_date")
    private LocalDate changeToDate;

    @Column(name = "current_active_flag")
    private String currentActiveFlag;

    @Column(name = "push_remove_status", length = 64)
    private String pushRemoveStatus;

    @Column(name = "exception_status", length = 64)
    private String exceptionStatus;

    @Column(name = "exception_reason", length = 64)
    private String exceptionReason;

    @Column(name = "exception_date")
    private java.util.Date exceptionDate;

    @Column(name = "push_status", length = 64)
    private String pushStatus;

    @Column(name = "push_date")
    private Date pushDate;

    @Column(name = "mmc_id")
    private Long mmcId;

    @Column(name = "to_remove_date")
    private Date toRemoveDate;

    @Column(name = "remarks", length = 64)
    private String remarks;

    @Column(name = "mail_status", length = 32)
    private String mailStatus;

    @Column(name = "self_allotment_qr_usagedate")
    private LocalDateTime selfAllotmentQrUsageDate;

    @Column(name = "self_allotment_qr_status", length = 32)
    private String selfAllotmentQrStatus;

    @Column(name = "self_allotment_qr_number", length = 32)
    private String selfAllotmentQrNumber;

    @Column(name = "comments")
    private String comments;

    @Column(name = "to_push_date")
    private Date toPushDate;
    @Column(name = "created_by", nullable = false, length = 20)
    public String createdBy;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;

    @Column(name = "modified_by", nullable = false, length = 20)
    protected String modifiedBy;

    @Column(name = "modified_at", nullable = false)
    protected LocalDateTime modifiedAt;

    @Column(name = "active_flag", nullable = false, length = 1)
    protected String activeFlag;


    @Transient
    String studentName;
    @Transient
    String messName;
}
