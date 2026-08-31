package com.iitm.hosteldine.model.mess;

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
@Table(name = "\"IIT_A_MESS_REBATE_WORKFLOW\"", schema = ModelConstants.SCHEMA)
public class MessRebateWorkflowEntity extends CommonEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "student_id", nullable = false, length = 32)
    private String studentId;

    @Column(name = "authority_type", length = 32)
    private String authorityType;

    @Column(name = "approval_level")
    private Integer approvalLevel;

    @Column(name = "guide_name", length = 50)
    private String guideName;

    @Column(name = "guide_email", length = 128)
    private String guideEmail;

    @Column(name = "authentication_type", length = 1)
    private String authenticationType;

    @Column(name = "approval_status", length = 16)
    private String approvalStatus;

    @Column(name = "cancel_status", nullable = false, length = 1)
    private String cancelStatus;

    @Column(name = "rejection_description", length = 1024)
    private String rejectionDescription;

    @Column(name = "approval_notes", length = 1024)
    private String approvalNotes;

}
