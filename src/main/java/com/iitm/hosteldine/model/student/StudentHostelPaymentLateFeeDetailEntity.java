package com.iitm.hosteldine.model.student;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "\"STUDENT_HOSTEL_PAYMENT_LATE_FEE_DETAILS\"", schema = ModelConstants.SCHEMA)
public class StudentHostelPaymentLateFeeDetailEntity extends CommonEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "student_id")
  private String studentId;

  @Column(name ="due_date")
  private LocalDate dueDate;
  
  @Column(name ="hostel_id")
  private Integer hostelId;

  @Column(name = "description")
  private String description;
}