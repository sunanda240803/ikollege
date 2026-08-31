package com.iitm.hosteldine.model.mess;

import java.time.LocalDate;

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
@Table(name = "\"MESS_MASTER\"", schema = ModelConstants.SCHEMA)
public class MessMasterEntity extends CommonEntity {
	@Id
    @Column(name = "mess_master_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mess_name", length = 128, nullable = false)
    private String messName;

    @Column(name = "description", length = 256)
    private String description;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "is_food_court")
    private Boolean isFoodCourt;

    @Column(name = "mess_allotted_capacity", nullable = true, columnDefinition = "int4 DEFAULT 0")
    private Integer messAllottedCapacity;

    @Column(name = "from_date")
    private LocalDate fromDate;

    @Column(name = "to_date")
    private LocalDate toDate;

    @Column(name = "is_jain_food")
    private Boolean isJainFood;

    @Column(name = "gender_option", length = 8)
    private String genderOption;

    @Column(name = "mess_order")
    private Integer messOrder=1;

    @Column(name = "mess_type", length = 20)
    private String messType;

    @Column(name = "mess_floor_name")
    private String messFloorName;

    @Column(name = "mess_head")
    private String messHead;

    @Column(name = "applicable_status")
    private Boolean applicableStatus;

    @Column(name = "girls_threshold_count")
    private Integer girlsThresholdCount;

    @Column(name = "sick_food_avail", nullable = true, columnDefinition = "bool DEFAULT false")
    private Boolean sickFoodAvail;
    
    @Column(name = "online_coupon", nullable = true, columnDefinition = "bool DEFAULT false")
    private Boolean onlineCoupon;

    @Column(name = "food_court_amount")
    private Double foodCourtAmount;

    @Column(name = "is_veg_nonveg")
    private String isVegNonVeg;
}
