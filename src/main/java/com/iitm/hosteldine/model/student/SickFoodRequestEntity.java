package com.iitm.hosteldine.model.student;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import java.time.LocalDate;

import com.iitm.hosteldine.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(name = "\"SICK_FOOD_REQUEST\"",  schema = ModelConstants.SCHEMA)
public class SickFoodRequestEntity  extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "student_id", nullable = false, length = 32)
    private String studentId;

    @Column(name = "mobile_num", nullable = false, length = 12)
    private String mobileNum;

    @Column(name = "delivery_address", nullable = false, length = 128)
    private String deliveryAddress;

    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;

    @Column(name = "mess_type", nullable = false, length = 16)
    private String messType;

    @Column(name = "mess_id")
    private Integer messId;

    @Column(name = "medical_reason", nullable = false, length = 256)
    private String medicalReason;

    @Column(name = "medical_proof_doc", length = 256)
    private String medicalProofDoc;

    @Column(name = "delivery_option", length = 64)
    private String deliveryOption;
   
	/*
	 * @ManyToOne(fetch = FetchType.LAZY)
	 * 
	 * @JoinColumn(name = "student_id", referencedColumnName = "student_id",
	 * insertable = false, updatable = false) private StudentDetailsInfo
	 * studentDetailsInfo;
	 */

   

}
