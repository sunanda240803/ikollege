package com.iitm.hosteldine.model.dashboard.student;

import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"IIT_W_WORKFLOW_MASTER\"", schema = "schooldev")
public class WorkflowMasterEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 32)
    @Column(name = "category", length = 32)
    private String category;

    @Size(max = 32)
    @Column(name = "authority_type", length = 32)
    private String authorityType;

    @Column(name = "approval_level")
    private Integer approvalLevel;

    @Size(max = 128)
    @Column(name = "email", length = 128)
    private String email;

    @Size(max = 64)
    @Column(name = "validator_name", length = 64)
    private String validatorName;

    @Size(max = 1)
    @Column(name = "authentication_type", length = 1)
    private String authenticationType;

}