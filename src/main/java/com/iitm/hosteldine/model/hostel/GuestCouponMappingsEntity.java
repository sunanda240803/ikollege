package com.iitm.hosteldine.model.hostel;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "\"IITM_GUEST_COUPON_MAPPINGS\"", schema = ModelConstants.SCHEMA)
public class GuestCouponMappingsEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(name = "coupon_number")
    private String couponNumber;

    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "validity_from_date")
    private LocalDate fromDate;

    @Column(name = "validity_to_date")
    private LocalDate toDate;

    @Column(name = "mess_id")
    private Long messId;

    @Column(name = "coupon_type")
    private String couponType;

    @Column(name = "coupon_used_status")
    private String usedStatus;

    @Column(name = "usage_date")
    private LocalDate usedDate;

    @Column(name = "usage_time")
    private LocalDateTime usedTime;
}
