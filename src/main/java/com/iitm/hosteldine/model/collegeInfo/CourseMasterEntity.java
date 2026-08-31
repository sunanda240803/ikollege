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

@Getter
@Setter
@Entity
@Table(name = "\"course_master\"", schema = ModelConstants.SCHEMA)
public class CourseMasterEntity extends CommonEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "course_master_id", nullable = false)
	private Long courseMasterId;
	
	@Column(name = "course_master_name", nullable = false, length = 100)
	private String courseMasterName;

	@Column(name = "description", nullable = true, length = 300)
	private String description;

	@Column(name = "degree_awarded", nullable = true, length = 50)
	private String degreeAwarded;

	@Column(name = "affiliation", nullable = false, length = 100)
	private String affiliation;

	@Column(name = "display_count", nullable = true)
	private Integer displayCount;

	@Column(name = "department_id", nullable = true)
	private Integer departmentId;

	@Column(name = "course_master_head")
	private String courseMasterHead;

}
