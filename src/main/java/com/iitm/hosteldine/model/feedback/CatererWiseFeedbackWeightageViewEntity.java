package com.iitm.hosteldine.model.feedback;

import com.iitm.hosteldine.constant.ModelConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Mapping for DB view
 */
@Getter
@Setter
@Entity
@Table(name = "catererwise_feedback_weightage_view", schema = ModelConstants.SCHEMA)
public class CatererWiseFeedbackWeightageViewEntity {
    @Id
    @Column(name = "mess_master_id")
    private Long messMasterId;

    @Size(max = 128)
    @Column(name = "mess_name", length = 128)
    private String messName;

    @Column(name = "feedback_quesid")
    private Integer feedbackQuesid;

    @Size(max = 200)
    @Column(name = "feedback_quesdesc", length = 200)
    private String feedbackQuesdesc;

    @Column(name = "feedback_weightage")
    private Integer feedbackWeightage;

    @Column(name = "feedback_score")
    private Long feedbackScore;

    @Column(name = "student_count")
    private Long studentCount;

    @Column(name = "overall_weightage", precision = 10, scale = 2)
    private BigDecimal overallWeightage;

    @Column(name = "mmc_n_id")
    private Long mmcNId;

    @Column(name = "mmc_d_dining_fromdate")
    private LocalDate mmcDDiningFromdate;

    @Column(name = "mmc_d_dining_todate")
    private LocalDate mmcDDiningTodate;

}