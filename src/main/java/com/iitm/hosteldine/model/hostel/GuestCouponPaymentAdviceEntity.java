package com.iitm.hosteldine.model.hostel;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


/**
 * The persistent class for the "IITM_GUEST_COUPON_PAYMENT_ADVICE" database table.
 *
 */
@Getter
@Setter
@Entity
@Table(name="\"IITM_GUEST_COUPON_PAYMENT_ADVICE\"", schema = ModelConstants.SCHEMA)
public class GuestCouponPaymentAdviceEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="request_id", nullable = false)
    private Long requestId;
    
    @Column(name="category")
    private String category;

    @Column(name="student_id")
    private String studentId;
    
    @Column(name="candidate_name")
    private String candidateName;

    @Column(name="hostel_id")
    private Long hostelId;
    
    @Column(name="room_number")
    private Integer roomNumber;
    
    @Column(name="mobile_number")
    private String mobileNumber;
    
    @Column(name="fac_department")
    private String facDepartment;

    @Column(name="fac_program")
    private String facProgram;

    @Column(name="mail_id")
    private String mailId;

    @Column(name="purpose")
    private String purpose;

    @Column(name="dining_from_date")
    private LocalDate diningFromDate;

    @Column(name="dining_to_date")
    private LocalDate diningToDate;
    
    @Column(name="mess_id")
    private Long messId;

    @Column(name="no_of_breakfast_coupons")
    private Integer noOfBreakfastCoupons;

    @Column(name="no_of_lunch_coupons")
    private Integer noOfLunchCoupons;
    
    @Column(name="no_of_dinner_coupons")
    private Integer noOfDinnerCoupons;
    
    @Column(name="breakfast_coupon_rate")
    private Integer breakfastCouponRate;
    
    @Column(name="lunch_coupon_rate")
    private Integer lunchCouponRate;
    
    @Column(name="dinner_coupon_rate")
    private Integer dinnerCouponRate;

    @Column(name="overall_amount")
    private Long overallAmount;

    @Column(name="payment_type")
    private String paymentType;
    
    @Column(name="payment_date")
    private LocalDate paymentDate;

    @Column(name="payment_reference_no")
    private String paymentReferenceNo;
    
    @Column(name="payment_amount")
    private Integer paymentAmount;

    @Column(name="payment_status")
    private String paymentStatus;

    @Column(name="approval_status")
    private String approvalStatus;
    
    @Column(name="print_status")
    private String printStatus;

    @Column(name="mail_status")
    private String mailStatus;
    
    @Column(name="veg_or_nonveg")
    private String vegOrNonVeg;
    
    @Column(name="bulk_coupon")
    private boolean bulkCoupon;

    @Column(name="no_of_snacks_coupons")
    private Integer noOfSnacksCoupons;

    @Column(name="snacks_coupon_rate")
    private Integer snacksCouponRate;

    @Column(name="config_discounted_amount")
    private Integer configDiscountedAmount;

    @Column(name="total_discounted_amount")
    private Integer totalDiscountedAmount;
    
}
