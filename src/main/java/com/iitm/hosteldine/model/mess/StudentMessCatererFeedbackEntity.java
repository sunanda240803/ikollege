package com.iitm.hosteldine.model.mess;

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
@Table(name = "\"STUDENT_MESS_CATERER_FEEDBACK\"", schema = ModelConstants.SCHEMA)
public class StudentMessCatererFeedbackEntity extends CommonEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "student_id", nullable = false, length = 30)
	private String studentId;

	@Column(name = "mess_master_id", nullable = false)
	private Long messMasterId;

	@Column(name = "feedback_quesid", nullable = false)
	private Integer feedbackQuesId;

	@Column(name = "feedback_score", nullable = false)
	private Integer feedbackScore;

	@Column(name = "mess_controller_id", nullable = false)
	private Long messControllerId;
}
