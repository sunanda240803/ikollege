package com.iitm.hosteldine.model.mess;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import java.time.LocalDate;

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
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"STUDENT_EXCEESS_MESS_DETAILS\"", schema = ModelConstants.SCHEMA)
public class StudentExcessMessDetailsEntity extends CommonEntity {
	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/*@Column(name = "student_id", length = 32)
	private String studentId;

	@Column(name = "mess_id", nullable = false)
	private Integer messId;*/

	@Column(name = "mmc_id")
	private Integer mmcId;

	@Column(name = "from_date")
	private LocalDate fromDate;

	@Column(name = "to_date")
	private LocalDate toDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id", referencedColumnName = "student_id", nullable = false)
	private StudentDetailsInfoEntity studentDetailsInfo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mess_id", referencedColumnName = "mess_master_id", nullable = false)
	private MessMasterEntity messMaster;
}
