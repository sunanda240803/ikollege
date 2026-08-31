package com.iitm.hosteldine.model.OtherCandidate;

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
@Table(name = "\"IIT_W_CANDIDATE_STAY_REQUEST_WORKFLOW\"", schema = ModelConstants.SCHEMA)
public class StayExtensionRequestWorkflowEntity extends CommonEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "candidate_id")
    private Long candidateId;

    @Column(name = "appointment_id")
    private Long appointmentId;
    
    @Column(name = "stay_id")
    private Long stayId;

    @Column(name = "category")
    private String category;

    @Column(name = "authority_type")
    private String authorityType;

    @Column(name = "approval_level")
    private Integer approvalLevel;

    @Column(name = "validator_name")
    private String validatorName;

    @Column(name = "validator_email")
    private String validatorEmail;

    @Column(name = "approval_status")
    private String approvalStatus;

    @Column(name = "authentication_type")
    private String authenticationType;


    @Column(name = "approval_notes")
    private String approvalNotes;
    
    @Column(name = "rejection_description")
    private String rejectionDescription;
    
}
