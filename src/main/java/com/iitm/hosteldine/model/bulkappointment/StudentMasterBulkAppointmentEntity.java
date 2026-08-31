package com.iitm.hosteldine.model.bulkappointment;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "\"IIT_W_STUDENT_MASTER_BULK_APPOINTMENT\"", schema = ModelConstants.SCHEMA)
public class StudentMasterBulkAppointmentEntity extends CommonEntity {

	@Id
	@Column(name = "bulk_appointment_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "description")
	private String description;

	@Column(name = "student_count")
	private Integer studentCount;

	@Column(name = "event_name")
	private String eventName;

	@Column(name = "from_date")
	private LocalDate fromDate;

	@Column(name = "to_date")
	private LocalDate toDate;

	@Column(name = "approval_status")
	private String approvalStatus;

	@Column(name = "no_of_maleparticipants")
	private Integer noOfMaleParticipants;

	@Column(name = "no_of_femaleparticipants")
	private Integer noOfFemaleParticipants;

	@Column(name = "dining")
	private Boolean dining;

	@Column(name = "breakfast_count")
	private Integer breakfastCount;

	@Column(name = "lunch_count")
	private Integer lunchCount;

	@Column(name = "dinner_count")
	private Integer dinnerCount;

	@Column(name = "session_period")
	private String sessionPeriod;

	@Column(name = "approval_notes")
	private String approvalNotes;

	// One-to-Many relationship with StudentBulkAppointmentEntity
	@OneToMany(mappedBy = "studentMasterBulkAppointment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<StudentBulkAppointmentEntity> studentBulkAppointments;

	// One-to-Many relationship with StudentBulkAppointmentDetailsEntity
	@OneToMany(mappedBy = "studentMasterBulkAppointment", cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
	private List<StudentBulkAppointmentDetailsEntity> studentBulkAppointmentDetails;
}