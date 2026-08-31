package com.iitm.hosteldine.model.OtherCandidate;

import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"IIT_W_CANDIDATE_WORKFLOW\"", schema = "schooldev")
public class CandidateWorkflowEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "application_id")
    private Long applicationId;

    @Column(name = "candidate_id")
    private Long candidateId;

    @Column(name = "authority_type", length = 32)
    private String authorityType;

    @Column(name = "approval_level")
    private Integer approvalLevel;

    @Column(name = "email", length = 128)
    private String email;

    @Column(name = "validator_name", length = 64)
    private String validatorName;

    @Column(name = "authentication_type", length = 1)
    private String authenticationType;

    @Column(name = "category", length = 32)
    private String category;

    @Column(name = "status", nullable = false, length = 32)
    private String status = "Pending";

    @Column(name = "rejection_description", length = 1024)
    private String rejectionDescription;

    @Column(name = "approval_notes", length = 1024)
    private String approvalNotes;
}