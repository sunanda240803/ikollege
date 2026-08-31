package com.iitm.hosteldine.model.OtherCandidate;

import java.time.LocalDate;

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
@Table(name = "\"IIT_W_CANDIDATE_STAY_REQUEST\"", schema = ModelConstants.SCHEMA)
public class StayExtensionRequestEntity extends CommonEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stay_id")
    private Long stayId;

    @Column(name = "candidate_id")
    private Long candidateId;

    @Column(name = "appointment_id")
    private Long appointmentId;

    @Column(name = "stay_from")
    private LocalDate stayFrom;

    @Column(name = "stay_to")
    private LocalDate stayTo;

    @Column(name = "dining")
    private Boolean dining;

    @Column(name = "description")
    private String description;

    @Column(name = "validating_authority")
    private String validatingAuthority;

    @Column(name = "validating_authority_email")
    private String validatingAuthorityEmail;

    @Column(name = "approval_status")
    private String approvalStatus;

    @Column(name = "rejection_description")
    private String rejectionDescription;

    @Column(name = "approval_date")
    private LocalDate approvalDate;

    @Column(name = "status_notes")
    private String statusNotes;
    
    @Column(name = "resend_date")
    private LocalDate resendDate;
    
    @Column(name = "mess_option")
    private String messOption;
    
    @Column(name = "hostel_id")
    private Integer hostelId;
    
    @Column(name = "room_no")
    private Integer roomNo;
    
}
