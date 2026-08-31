package com.iitm.hosteldine.model.OtherCandidate;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"IIT_W_CANDIDATE_STAY_REQUEST_WORKFLOW\"", schema = ModelConstants.SCHEMA)
public class CandidateStayRequestWorkflowEntity extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;

    @Column(name = "appointment_id")
    private Long appointmentId;

    @Column(name = "stay_id")
    private Long stayId;

    @Column(name = "category", length = 32)
    private String category;

    @Column(name = "authority_type", length = 64)
    private String authorityType;

    @Column(name = "approval_level")
    private Integer approvalLevel;

    @Column(name = "validator_name", nullable = false, length = 50)
    private String validatorName;

    @Column(name = "validator_email", nullable = false, length = 128)
    private String validatorEmail;

    @Column(name = "approval_status", length = 16)
    private String approvalStatus;

    @Column(name = "authentication_type", length = 1)
    private String authenticationType;

    @Column(name = "approval_notes", length = 1024)
    private String approvalNotes;

    @Column(name = "rejection_description", length = 1024)
    private String rejectionDescription;
}
