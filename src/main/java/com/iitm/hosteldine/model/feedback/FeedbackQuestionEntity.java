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
@Table(name = "\"FEEDBACK_QUESTION\"",  schema = ModelConstants.SCHEMA) 
public class FeedbackQuestionEntity extends CommonEntity{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_quesid", nullable = false)
    private Long feedbackQuesId;

    @Column(name = "feedback_quesdesc", length = 200)
    private String feedbackQuesDesc;

    @Column(name = "feedback_weightage")
    private Integer feedbackWeightage;

}
