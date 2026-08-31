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
@Table(name = "\"IITM_GUEST_COUPON_CONFIG\"", schema = ModelConstants.SCHEMA)
public class GuestCouponConfigEntity extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "config_id", nullable = false)
    private Long id;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "valid_to_date")
    private LocalDate validToDate;

    @Column(name = "breakfast_amount")
    private Integer breakfastAmount;

    @Column(name = "lunch_amount")
    private Integer lunchAmount;

    @Column(name = "dinner_amount")
    private Integer dinnerAmount;

    @Column(name = "snacks_amount")
    private Integer snacksAmount;

    @Column(name = "description")
    private String description;

    @Column(name = "validity_days")
    private Integer validityDays;

    @Column(name = "category")
    private String category;

}
