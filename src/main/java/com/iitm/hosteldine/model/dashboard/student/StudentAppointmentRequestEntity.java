package com.iitm.hosteldine.model.dashboard.student;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Table;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;

@Setter
@Getter
@ToString
@Entity
@DynamicInsert
@Table(name = "\"IIT_W_STUDENT_APPOINTMENT_REQUEST\"", schema = ModelConstants.SCHEMA)
public class StudentAppointmentRequestEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id", nullable = false, length = 25)
    private Long id;

    @Column(name = "student_id", length = 30)
    private String studentId;

    @Column(name = "appointment_from")
    private LocalDate appointmentFrom;

    @Column(name = "appointment_to")
    private LocalDate appointmentTo;

    @Column(name = "stay_from")
    private LocalDate stayFrom;

    @Column(name = "stay_to")
    private LocalDate stayTo;

    @Column(name = "gross_pay")
    private Double grossPay;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "category_others", length = 64)
    private String categoryOthers;

    @Column(name = "dining", length = 32)
    private String dining;

    @Column(name = "validating_authority", length = 64)
    private String validatingAuthority;

    @Column(name = "validating_authority_email", length = 128)
    private String validatingAuthorityEmail;

    @Column(name = "hostel_rules")
    private Boolean hostelRules;

    @Column(name = "applicable_charges")
    private Boolean applicableCharges;

    @Column(name = "status", length = 32)
    private String status;

    @Column(name = "dining_others", length = 128)
    private String diningOthers;

    @Column(name = "occupancy", length = 50)
    private String occupancy;

    @Column(name = "reject_description", length = 1024)
    private String rejectDescription;

    @Column(name = "approval_date")
    private LocalDate approvalDate;

    @Column(name = "approval_notes")
    private String approvalNotes;

    @Column(name = "resend_date")
    private LocalDate resendDate;

    @Column(name = "status_notes")
    private String statusNotes;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "thesis_status")
    private String thesisStatus;

    @Column(name = "thesis_submitted_date")
    private LocalDate thesisSubmittedDate;

    @Column(name = "hod_name", length = 64)
    private String hodName;

    @Column(name = "hod_email", length = 128)
    private String hodEmail;

    @Column(name = "admission_date")
    private LocalDate admissionDate;

    @Column(name = "cancel_description", length = 1024)
    private String cancelDescription;

}
