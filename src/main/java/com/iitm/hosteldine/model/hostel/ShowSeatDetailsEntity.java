package com.iitm.hosteldine.model.hostel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import java.sql.Timestamp;

import com.iitm.hosteldine.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"SHOW_SEAT_DETAILS\"", schema = ModelConstants.SCHEMA)
public class ShowSeatDetailsEntity extends CommonEntity{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", referencedColumnName = "id")
    private ShowMasterEntity show;

    @Column(name = "seat_name", length = 63)
    private String seatName;

    @Column(name = "discounted_amount")
    private Double discountedAmount;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "discount_status", length = 32)
    private String discountStatus;

    @Column(name = "currently_active", length = 1)
    private String currentlyActive;

    @Column(name = "is_available")
    private Boolean isAvailable;
}
