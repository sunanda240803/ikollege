package com.iitm.hosteldine.model.feedback;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

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
@Table(name = "\"FEEDBACK_QUALIFIER\"", schema = ModelConstants.SCHEMA) 
public class FeedbackQualifierEntity extends CommonEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "feedback_id", nullable = false)
	private Integer feedbackId;

	@Column(name = "feedback_desc", length = 20)
	private String feedbackDesc;

	@Column(name = "feedback_points")
	private Integer feedbackPoints;

}
