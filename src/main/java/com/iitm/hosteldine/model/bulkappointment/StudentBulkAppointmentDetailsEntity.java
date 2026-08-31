package com.iitm.hosteldine.model.bulkappointment;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "\"IIT_W_STUDENT_BULK_APPOINTMENT_DETAILS\"", schema = ModelConstants.SCHEMA)
public class StudentBulkAppointmentDetailsEntity extends CommonEntity {

	@Id
	@Column(name = "detail_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "bulk_appointment_id", referencedColumnName = "bulk_appointment_id")
	private StudentMasterBulkAppointmentEntity studentMasterBulkAppointment;

	@Column(name = "stay_from")
	private LocalDate stayFrom;

	@Column(name = "stay_to")
	private LocalDate stayTo;

	@Column(name = "no_of_maleparticipants")
	private Integer noOfMaleParticipants;

	@Column(name = "no_of_femaleparticipants")
	private Integer noOfFemaleParticipants;

	@Column(name = "dining")
	private String dining;

	@Column(name = "breakfast_count")
	private Integer breakfastCount;

	@Column(name = "lunch_count")
	private Integer lunchCount;

	@Column(name = "dinner_count")
	private Integer dinnerCount;

	@Column(name = "session_period")
	private String sessionPeriod;
}