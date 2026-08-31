package com.iitm.hosteldine.model.mess;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"STUDENT_MESS_LOGIN_ISSUE_PRIORITY\"",  schema = ModelConstants.SCHEMA) 
public class StudentMessLoginIssuePriorityEntity extends CommonEntity {

	@EmbeddedId
	StudentMessLoginIssuePriorityId id;

	@Column(name = "physically_challenged", length = 8)
	private String physicallyChallenged;

	@Column(name = "student_name", length = 120)
	private String studentName;

	@Column(name = "gender", length = 1)
	private String gender;

	@Column(name = "description")
	private String description;

	@Column(name = "hostel_id", nullable = false)
	private Integer hostelId;

	@Column(name = "room_no", nullable = false, length = 100)
	private String roomNo;

	@Column(name = "sem_mon", length = 10)
	private String semMon;
}
