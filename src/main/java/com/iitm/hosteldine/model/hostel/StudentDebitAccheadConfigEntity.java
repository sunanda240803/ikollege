package com.iitm.hosteldine.model.hostel;

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
@Table(name = "\"STUDENT_DEBIT_ACCHEAD_CONFIG\"", schema = ModelConstants.SCHEMA)
public class StudentDebitAccheadConfigEntity extends CommonEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "acchead_name", nullable = false, length = 18)
	private String accheadName;

	@Column(name = "acchead", nullable = false, length = 32)
	private String acchead;

	@Column(name = "credit_or_debit", length = 1)
	private String creditOrDebit;

}
