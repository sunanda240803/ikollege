package com.iitm.hosteldine.model.student;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
@Table(name = "\"IITM_CONVOCATION_ACCOMMODATION\"", schema = ModelConstants.SCHEMA)
public class ConvocationAccommodationEntity extends CommonEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "student_id", nullable = false, length = 16)
    private String studentId;

    @Column(name = "student_name", nullable = false, length = 64)
    private String studentName;

    @Column(name = "gender", nullable = false, length = 8)
    private String gender;

    @Column(name = "mail_id", nullable = false, length = 64)
    private String mailId;

    @Column(name = "menu_type", nullable = false, length = 32)
    private String menuType;

    @Column(name = "accommodation_status")
    protected Boolean accommodationStatus;

    @Column(name = "complimentary_coupons", length = 16)
    private String complimentaryCoupons;

    @Column(name = "dining_from_date", nullable = false)
    private LocalDate diningFromDate;

    @Column(name = "dining_to_date", nullable = false)
    private LocalDate diningToDate;

    @Column(name = "additional_no_of_coupons")
    private Integer additionalNoOfCoupons;

    @Column(name = "overall_amount", nullable = false)
    private Double overallAmount;

    @Column(name = "order_no", length = 50)
    private String orderNo;

    @Column(name = "payment_type", length = 64)
    private String paymentType;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "payment_reference_no", length = 64)
    private String paymentReferenceNo;

    @Column(name = "ccav_reference_no", length = 50)
    private String ccavReferenceNo;

    @Column(name = "payment_amount")
    private Double paymentAmount;

    @Column(name = "payment_status", length = 32)
    private String paymentStatus;

    @Column(name = "trans_fee")
    private Double transFee;

    @Column(name = "service_tax")
    private Double serviceTax;

    @Column(name = "status_message")
    private String statusMessage;

    @Column(name = "hostel_name")
    private String hostelName;

    @Column(name = "retry_count")
    private Integer retryCount;

}
