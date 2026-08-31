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
@Table(name = "\"IIT_W_CANDIDATE_STAY_REQUEST\"", schema = ModelConstants.SCHEMA)
public class CandidateStayRequestEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stay_id", nullable = false)
    private Long stayId;

    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;

    @Column(name = "appointment_id", nullable = false)
    private Long appointmentId;

    @Column(name = "stay_from")
    private LocalDate stayFrom;

    @Column(name = "stay_to")
    private LocalDate stayTo;

    @Column(name = "dining")
    private Boolean dining;

    @Column(name = "description", length = 1024)
    private String description;

    @Column(name = "validating_authority", length = 50)
    private String validatingAuthority;

    @Column(name = "validating_authority_email", length = 32)
    private String validatingAuthorityEmail;

    @Column(name = "approval_status", length = 32)
    private String approvalStatus;

    @Column(name = "rejection_description", columnDefinition = "text")
    private String rejectionDescription;

    @Column(name = "approval_date")
    private LocalDate approvalDate;

    @Column(name = "status_notes", columnDefinition = "text")
    private String statusNotes;

    @Column(name = "resend_date")
    private LocalDateTime resendDate;

    @Column(name = "mess_option", length = 32)
    private String messOption;

    @Column(name = "hostel_id")
    private Integer hostelId;

    @Column(name = "room_no")
    private Integer roomNo;
}