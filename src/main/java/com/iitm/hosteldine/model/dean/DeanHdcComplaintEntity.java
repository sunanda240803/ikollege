package com.iitm.hosteldine.model.dean;

import java.io.Serializable;
import java.util.Date;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"IIT_HDC_COMPLAINT_FORMDETAILS\"", schema = ModelConstants.SCHEMA)
public class DeanHdcComplaintEntity extends CommonEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "hdc_id", nullable = false)
	private Long id;

	@Column(name = "warden_id", nullable = false)
	private Long wardenId;

	@Column(name = "hostel_id", nullable = false)
	private Long hostelId;

	@Column(name = "room_no")
	private Integer roomNo;

	@Column(name = "student_id", length = 30, nullable = false)
	private String studentId;

	@Column(name = "student_name")
	private String studentName;

	@Column(name = "violation")
	private String violation;

	@Column(name = "wardern_plea")
	private String wardenPlea;

	@Column(name = "warden_decision")
	private String wardenDecision;

	@Column(name = "warden_remarks")
	private String wardenRemarks;

	@Column(name = "follow_up_report")
	private String followUpReport;

	@Column(name = "penality_amount")
	private Double penaltyAmount;

	@Column(name = "penality_due_date")
	private Date penaltyDueDate;

	@Column(name = "paid_amount")
	private Double paidAmount;

	@Column(name = "payment_description")
	private String paymentDescription;

	@Column(name = "penality_status")
	private String penaltyStatus;

	@Column(name = "payment_reference_no")
	private String paymentReferenceNumber;

	@Column(name = "parent_mail_status")
	private Boolean parentMailStatus;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "involved_stud_ids")
	private String involvedStudentId;

	@Column(name = "day_scholar_involve")
	private String dayScholarInvolve;

	@Column(name = "category")
	private String category;

}
