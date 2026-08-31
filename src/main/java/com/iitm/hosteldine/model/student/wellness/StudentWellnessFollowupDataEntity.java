package com.iitm.hosteldine.model.student.wellness;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"STUDENT_WELLNESS_FOLLOWUP_DATA\"", schema = ModelConstants.SCHEMA)
public class StudentWellnessFollowupDataEntity extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visit_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "wellness_id")
    private StudentWellnessCategoricalDataEntity wellness;

    @Column(name = "visit_date")
    private LocalDate visitDate;

    @Column(name = "no_of_visit")
    private Integer noOfVisit;

    @Column(name = "interaction_mode", length = 128)
    private String interactionMode;

    @Column(name = "session_start_time")
    private LocalDateTime sessionStartTime;

    @Column(name = "duration")
    private Short duration;

    @Column(name = "concerns_discussed")
    private String concernsDiscussed;

    @Column(name = "future_action_plan")
    private String futureActionPlan;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @Column(name = "mail_status", length = 16)
    private String mailStatus;

    @Column(name = "visit_status")
    private String visitStatus;

}
