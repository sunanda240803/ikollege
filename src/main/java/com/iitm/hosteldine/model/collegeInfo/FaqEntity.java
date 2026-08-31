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
@Table(name = "\"IIT_W_FAQ_DETAILS\"", schema = ModelConstants.SCHEMA)
public class FaqEntity extends CommonEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;
	
	@Column(name = "question", nullable = false, length = 300)
	private String question;

	@Column(name = "answer", nullable = true, length = 300)
	private String answer;

}
