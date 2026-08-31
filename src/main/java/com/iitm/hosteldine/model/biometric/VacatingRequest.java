package com.iitm.hosteldine.model.biometric;

import com.iitm.hosteldine.constant.ModelConstants;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "\"IITMSTUDENT_HOSTEL_ROOM_VACATING_REQUEST\"", schema = ModelConstants.SCHEMA)
public class VacatingRequest {
    @Id
    @Column(name = "id")
    private Long Id;
    @Column(name = "student_id")
    private String studentId;
    @Column(name = "vacating_date")
    private Date vacatingDate;
    @Column(name = "active_flag")
    private String activeFlag;
    @Column(name = "school_id")
    private Integer schoolId;
    @Column(name = "hostel_or_warden_approval_status")
    private String hostelOrWardenApprovalStatus;
}
