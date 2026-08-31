package com.iitm.hosteldine.model.OtherCandidate;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import com.iitm.hosteldine.model.hostel.HostelFloorMasterEntity;
import com.iitm.hosteldine.model.hostel.HostelRoomInfoEntity;
import com.iitm.hosteldine.model.mess.MessMasterEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"IIT_PS_TEMP_ACCOM_PAYMENT_ADVICE\"", schema = ModelConstants.SCHEMA)
@Getter
@Setter
public class TempAccomPaymentAdviceEntity extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", referencedColumnName = "request_id" )
	private CandidateAppointmentRequestEntity candidateRequest;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "payment_dummy_no", length = 32)
    private String paymentDummyNo;

    @Column(name = "hostel_pay_from_date")
    private LocalDate hostelPayFromDate;

    @Column(name = "hostel_pay_to_date")
    private LocalDate hostelPayToDate;

    @Column(name = "hostel_total_no_of_days")
    private Integer hostelTotalNoOfDays;

    @Column(name = "hostel_rate_per_day")
    private Integer hostelRatePerDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hostel_id", referencedColumnName = "floor_id" )
    private HostelFloorMasterEntity hostelFloorMaster; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", referencedColumnName = "room_id" )
    private HostelRoomInfoEntity hostelRoomInfo; 

    @Column(name = "seat", length = 1)
    private String seat;

    @Column(name = "mess_pay_from_date")
    private LocalDate messPayFromDate;

    @Column(name = "mess_pay_to_date")
    private LocalDate messPayToDate;

    @Column(name = "no_of_breakfast_coupons")
    private Integer noOfBreakfastCoupons;

    @Column(name = "no_of_lunch_coupons")
    private Integer noOfLunchCoupons;

    @Column(name = "no_of_dinner_coupons")
    private Integer noOfDinnerCoupons;

    @Column(name = "breakfast_coupon_rate")
    private Integer breakfastCouponRate;

    @Column(name = "lunch_coupon_rate")
    private Integer lunchCouponRate;

    @Column(name = "dinner_coupon_rate")
    private Integer dinnerCouponRate;

    @Column(name = "total_breakfast_amount")
    private Long totalBreakfastAmount;

    @Column(name = "total_lunch_amount")
    private Long totalLunchAmount;

    @Column(name = "total_dinner_amount")
    private Long totalDinnerAmount;

    @Column(name = "overall_amount")
    private Long overallAmount;

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mess_id", referencedColumnName = "mess_master_id" )
	private MessMasterEntity messMaster;

    @Column(name = "payment_status", length = 32)
    private String paymentStatus;

    @Column(name = "payment_type_1", length = 64)
    private String paymentType1;

    @Column(name = "payment_reference_no_1", length = 64)
    private String paymentReferenceNo1;

    @Column(name = "payment_amount_1")
    private Integer paymentAmount1;

    @Column(name = "payment_date_1")
    private LocalDateTime paymentDate1;

    @Column(name = "card_status", length = 32)
    private String cardStatus;

    @Column(name = "card_charges")
    private Integer cardCharges;

    @Column(name = "payment_amount_2")
    private Integer paymentAmount2;

    @Column(name = "payment_type_2")
    private String paymentType2;

    @Column(name = "payment_reference_no_2")
    private String paymentReferenceNo2;

    @Column(name = "payment_date_2")
    private LocalDateTime paymentDate2;

    @Column(name = "messacc_amount")
    private Integer messaccAmount;

    @Column(name = "payment_approval_status", length = 32)
    private String paymentApprovalStatus;

    @Column(name = "payment_approval_date")
    private LocalDateTime paymentApprovalDate;

}
