package com.iitm.hosteldine.model.collegeInfo;

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

@Setter
@Getter
@Entity
@Table(name = "\"departments_details\"" ,schema = ModelConstants.SCHEMA)
public class DepartmentEntity extends CommonEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "department_id", nullable = false)
	private Long departmentId;
	@Column(name = "department_name", nullable = false, length = 100)
	private String departmentName;
	@Column(name = "department_description")
	private String departmentDescription;
	@Column(name = "department_head")
	private String departmentHead;
	@Column(name = "department_assistant_head")
	private String departmentAssistantHead;
	

}
