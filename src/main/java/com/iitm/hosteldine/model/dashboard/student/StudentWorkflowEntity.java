package com.iitm.hosteldine.model.dashboard.student;

import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Getter
@Setter
@Entity
@DynamicInsert
@Table(name = "\"IIT_W_STUDENT_WORKFLOW\"", schema = "schooldev")
public class StudentWorkflowEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "request_id")
    private Long requestId;

    @Size(max = 32)
    @Column(name = "student_id", length = 32)
    private String studentId;

    @Size(max = 32)
    @Column(name = "authority_type", length = 32)
    private String authorityType;

    @Column(name = "approval_level")
    private Integer approvalLevel;

    @Size(max = 128)
    @Column(name = "validator_email", length = 128)
    private String validatorEmail;

    @Size(max = 64)
    @Column(name = "validator_name", length = 64)
    private String validatorName;

    @Size(max = 1)
    @Column(name = "authentication_type", length = 1)
    private String authenticationType;

    @Size(max = 32)
    @Column(name = "category", length = 32)
    private String category;

    @Size(max = 64)
    @NotNull
    @ColumnDefault("'Pending'")
    @Column(name = "status", nullable = false, length = 64)
    private String status;

    @Size(max = 1024)
    @Column(name = "reject_description", length = 1024)
    private String rejectDescription;

    @Size(max = 1024)
    @Column(name = "approval_notes", length = 1024)
    private String approvalNotes;

}