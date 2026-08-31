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
@Table(name = "\"STUDENT_ROLLNO_CHANGE\"", schema = ModelConstants.SCHEMA)
public class StudentRollnoChangeEntity extends CommonEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "studentid", nullable = false, length = 30)
	private String studentid;

	@Column(name = "new_roll_no", nullable = false, length = 30)
	private String newRollNo;

	@Column(name = "file_upload", length = 300)
	private String fileUpload;

	@Column(name = "status", length = 300)
	private String status;
	
}
