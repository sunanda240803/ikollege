package com.iitm.hosteldine.model.dashboard.student;

import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"IIT_W_VACATING_HOSTEL_STUDENT_WORKFLOW\"", schema = "schooldev")
public class VacatingHostelStudentWorkflowEntity extends CommonEntity {
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
    @Column(name = "approval_email", length = 128)
    private String approvalEmail;

    @Size(max = 64)
    @Column(name = "approval_name", length = 64)
    private String approvalName;

    @Size(max = 1)
    @Column(name = "authentication_type", length = 1)
    private String authenticationType;

    @Size(max = 32)
    @Column(name = "category", length = 32)
    private String category;

    @Size(max = 64)
    @NotNull
    @Column(name = "status", nullable = false, length = 64)
    private String status;
}