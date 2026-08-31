package com.iitm.hosteldine.model.student;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"STUDENT_HOSTEL_ENROLLMENT_CONFIGURATION\"", schema = ModelConstants.SCHEMA)
public class StudentHostelEnrollmentConfigurationEntity extends CommonEntity {
  @Id
  @GeneratedValue(strategy =  GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "ms_phd_amount")
  private Double msPhsAmount;

  @Column(name = "btech_amount")
  private Double bTechAmount;

  @Column(name = "sc_st_amount")
  private Double scStAmount;

  @Column(name = "description")
  private String description;

}