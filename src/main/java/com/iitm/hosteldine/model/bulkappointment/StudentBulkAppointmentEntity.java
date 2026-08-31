package com.iitm.hosteldine.model.bulkappointment;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "\"IIT_W_STUDENT_BULK_APPOINTMENT\"", schema = ModelConstants.SCHEMA)
public class StudentBulkAppointmentEntity extends CommonEntity {

	@Id
	@Column(name = "appointment_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "bulk_appointment_id", referencedColumnName = "bulk_appointment_id")
	private StudentMasterBulkAppointmentEntity studentMasterBulkAppointment;

	@Column(name = "student_id")
	private String studentId;

	@Column(name = "appointment_from")
	private LocalDate appointmentFrom;

	@Column(name = "appointment_to")
	private LocalDate appointmentTo;

	@Column(name = "stay_from")
	private LocalDate stayFrom;

	@Column(name = "stay_to")
	private LocalDate stayTo;

	@Column(name = "category")
	private String category;

	@Column(name = "dining")
	private String dining;

	@Column(name = "validating_authority")
	private String validatingAuthority;

	@Column(name = "validating_authority_email")
	private String validatingAuthorityEmail;

	@Column(name = "student_name")
	private String studentName;

	@Column(name = "role_played_during_stay")
	private String rolePlayedDuringStay;

	@Column(name = "student_designation")
	private String studentDesignation;

	@Column(name = "gender")
	private String gender;

	public StudentBulkAppointmentEntity(){
		super();
	}
}