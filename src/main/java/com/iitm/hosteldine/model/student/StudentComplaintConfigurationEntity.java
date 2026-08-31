package com.iitm.hosteldine.model.student;

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
@Table(name = "\"STUDENT_COMPLAINT_CONFIGURATION\"", schema = ModelConstants.SCHEMA)
public class StudentComplaintConfigurationEntity extends CommonEntity {
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "id", nullable = false)
	    private Long id;

	    @Column(name = "complaint_type", length = 128)
	    private String complaintType;

	    @Column(name = "complaint_name", columnDefinition = "TEXT")
	    private String complaintName;

	    @Column(name = "config_mail_id", length = 128)
	    private String configMailId;

	    @Column(name = "complaint_order", nullable = true)
	    private Integer complaintOrder;



}
