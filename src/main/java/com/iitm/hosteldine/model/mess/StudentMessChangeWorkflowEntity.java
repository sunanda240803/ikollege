package com.iitm.hosteldine.model.mess;

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
@Table(name = "\"STUDENT_MESS_CHANGE_WORKFLOW\"", schema = ModelConstants.SCHEMA)
public class StudentMessChangeWorkflowEntity extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "student_id", length = 32)
    private String studentId;

    @Column(name = "originally_mess_from_date")
    private LocalDate originallyMessFromDate;

    @Column(name = "originally_mess_to_date")
    private LocalDate originallyMessToDate;

    @Column(name = "originally_mess_id", nullable = false)
    private Long originallyMessId;

    @Column(name = "requested_mess_from_date")
    private LocalDate requestedMessFromDate;

    @Column(name = "requested_mess_to_date")
    private LocalDate requestedMessToDate;

    @Column(name = "requested_mess_id", nullable = false)
    private Long requestedMessId;

    @Column(name = "approved_by", length = 32)
    private String approvedBy;

    @Column(name = "approval_status", length = 32)
    private String approvalStatus;

    @Column(name = "approval_date", length = 32)
    private String approvalDate;

    @Column(name = "requested_date", length = 32)
    private String requestedDate;

    @Column(name = "description", length = 128)
    private String description;

    @Column(name = "description1", length = 128)
    private String description1;

    @Column(name = "mmc_id")
    private Long mmcId;
}
