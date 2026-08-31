package com.iitm.hosteldine.model.mess;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"STUDENT_MESS_DETAILS\"", schema = ModelConstants.SCHEMA)
public class StudentMessDetailsEntity extends CommonEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/*@Column(name = "student_id", length = 32)
	private String studentId;*/

	/*@Column(name = "mess_id", nullable = false)
	private Integer messId;*/

	@Column(name = "from_date")
	private LocalDate fromDate;

	@Column(name = "to_date")
	private LocalDate toDate;

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
	private LocalDate exceptionDate;

	@Column(name = "push_status", length = 64)
	private String pushStatus;

	@Column(name = "push_date")
	private LocalDate pushDate;

	@Column(name = "mmc_id")
	private Long mmcId;

	@Column(name = "to_remove_date")
	private LocalDate toRemoveDate;

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
	private LocalDate toPushDate;

	@Column(name = "description")
	private String description;

	// Relationships
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({@JoinColumn(name = "mess_id", referencedColumnName = "mess_master_id" ) })
	private MessMasterEntity messMaster;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "student_id", referencedColumnName = "student_id", updatable = false)})
	private StudentDetailsInfoEntity studentDetailsInfo;

}
