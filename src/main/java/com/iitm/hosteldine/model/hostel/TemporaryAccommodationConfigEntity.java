package com.iitm.hosteldine.model.hostel;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "\"IIT_PS_TEMP_ACCOM_CATEGORY_CONFIG\"", schema = ModelConstants.SCHEMA)
public class TemporaryAccommodationConfigEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "description")
    private String description;

    @Column(name = "acc_stay_amnt")
    private Integer accStayAmnt;

    @Column(name = "breakfast_coupon")
    private Integer breakfastCoupon;

    @Column(name = "lunch_coupon")
    private Integer lunchCoupon;

    @Column(name = "dinner_coupon")
    private Integer dinnerCoupon;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "rebate_charges")
    private Integer rebateCharges;
}