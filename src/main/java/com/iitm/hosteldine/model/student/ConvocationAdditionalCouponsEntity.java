package com.iitm.hosteldine.model.student;

import java.time.LocalDate;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"IITM_CONVOCATION_ADDITIONAL_COUPONS\"", schema = ModelConstants.SCHEMA)
public class ConvocationAdditionalCouponsEntity extends CommonEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "convocation_id", referencedColumnName = "id")
	private ConvocationAccommodationEntity convocation;

	@Column(name = "dining_date", nullable = false)
	private LocalDate diningDate;

	@Column(name = "no_of_breakfast_coupon")
	private Integer noOfBreakfastCoupon;

	@Column(name = "no_of_lunch_coupon")
	private Integer noOfLunchCoupon;

	@Column(name = "no_of_dinner_coupon")
	private Integer noOfDinnerCoupon;

}
