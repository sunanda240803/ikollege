package com.iitm.hosteldine.model.student;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"STUDENT_COMPLAINT_DETAILS\"", schema = ModelConstants.SCHEMA)
public class StudentComplaintDetailsEntity extends CommonEntity {
	     @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "complaint_id")
	    private Long complaintId;

	    @Column(name = "student_id", nullable = false, length = 32)
	    private String studentId;

	    @Column(name = "stu_complaint_type", length = 128)
	    private String stuComplaintType;

	    @Column(name = "complaints", length = 128)
	    private String complaints;

	    @Column(name = "complaint_desc")
	    private String complaintDesc;

	    @Column(name = "file_upload")
	    private String fileUpload;

	    @Column(name = "mail_id", length = 128)
	    private String mailId;

		
	 
		 


}
