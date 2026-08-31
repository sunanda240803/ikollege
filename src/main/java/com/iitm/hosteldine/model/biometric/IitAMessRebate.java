package com.iitm.hosteldine.model.biometric;

import com.iitm.hosteldine.constant.ModelConstants;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "\"IIT_A_MESS_REBATE\"", schema = ModelConstants.SCHEMA)
public class IitAMessRebate {
    @Id
    @Column(name = "id")
    Long id;
    @Column(name="student_id")
    String studentId;
    @Column(name = "rebate_from")
    Date rebateFrom;
    @Column(name = "rebate_to")
    Date rebateTo;
    @Column(name = "rebate_reason")
    String rebateReason;
    @Column(name = "cancel_status")
    String cancelStatus;
    @Column(name = "active_flag")
    String activeFlag;
    @Column(name = "school_id")
    Integer schoolId;
    @Column(name = "approval_status")
    String approvalStatus;


}
