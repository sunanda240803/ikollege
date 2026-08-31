package com.iitm.hosteldine.model.student.wellness;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import java.time.LocalDate;
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
@Table(name = "\"STUDENT_WELLNESS_CATEGORICAL_DATA\"", schema = ModelConstants.SCHEMA)
public class StudentWellnessCategoricalDataEntity extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wellness_id", nullable = false)
    private Long id;

    @Column(name = "student_id", length = 128)
    private String studentId;

    @Column(name = "referral_date")
    private LocalDate referralDate;

    @Column(name = "referral_type", length = 128)
    private String referralType;

    @Column(name = "referral_by", length = 1024)
    private String referralBy;

    @Column(name = "referral_email", length = 1024)
    private String referralEmail;

    @Column(name = "referral_phone", length = 128)
    private String referralPhone;

    @Column(name = "referral_others_description")
    private String referralOthersDescription;

    @Column(name = "coordinated_name", length = 1024)
    private String coordinatedName;

    @Column(name = "coordinated_email", length = 1024)
    private String coordinatedEmail;

    @Column(name = "concern_type", length = 128)
    private String concernType;

    @Column(name = "concern_others_description")
    private String concernOthersDescription;

    @Column(name = "self_harm")
    private Boolean selfHarm;

    @Column(name = "self_harm_type", length = 128)
    private String selfHarmType;

    @Column(name = "psychiatric_consultation")
    private Boolean psychiatricConsultation;

    @Column(name = "psychiatric_name", length = 1024)
    private String psychiatricName;

    @Column(name = "additional_details")
    private String additionalDetails;

    @Column(name = "referral_landline_num", length = 128)
    private String referralLandlineNum;

    @Column(name = "other_stud_name", length = 128)
    private String otherStudName;

    @Column(name = "other_stud_email", length = 32)
    private String otherStudEmail;

    @Column(name = "other_stud_phone", length = 16)
    private String otherStudPhone;

    @Column(name = "category", length = 16)
    private String category;
    
}
