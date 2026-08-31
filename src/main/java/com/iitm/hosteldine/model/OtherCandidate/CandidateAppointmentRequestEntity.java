package com.iitm.hosteldine.model.OtherCandidate;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "\"IIT_W_CANDIDATE_APPOINTMENT_REQUEST\"", schema = ModelConstants.SCHEMA)
public class CandidateAppointmentRequestEntity extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id", nullable = false)
    private Long id;

    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;

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

    @Column(name = "category", length = 16)
    private String category;

    @Column(name = "category_others", length = 64)
    private String categoryOthers;

    @Column(name = "dining")
    private Boolean dining;

    @Column(name = "occupancy", length = 16)
    private String occupancy;

    @Column(name = "validating_authority", length = 50)
    private String validatingAuthority;

    @Column(name = "validating_authority_email", length = 128)
    private String validatingAuthorityEmail;

    @Column(name = "hostel_management")
    private Boolean hostelManagement;

    @Column(name = "documents_uploaded")
    private Boolean documentsUploaded;

    @Column(name = "applicable_charges")
    private Boolean applicableCharges;

    @Column(name = "approval_status", length = 16)
    private String approvalStatus;

    @Column(name = "rejection_description", length = 1024)
    private String rejectionDescription;

    @Column(name = "approval_date")
    private LocalDate approvalDate;

    @Column(name = "resend_date")
    private LocalDateTime resendDate;

    @Column(name = "status_notes")
    private String statusNotes;

    @Column(name = "program_dept", length = 1024)
    private String programDept;

    @Column(name = "application_no", length = 20)
    private String applicationNo;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "accom_priority")
    private Long accomPriority;

    @Column(name = "mess_option", length = 32)
    private String messOption;

}
