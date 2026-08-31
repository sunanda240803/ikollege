package com.iitm.hosteldine.dto.hostel;

import java.util.List;

import com.iitm.hosteldine.model.hostel.GuestCouponMappingsEntity;
import com.iitm.hosteldine.model.hostel.GuestCouponPaymentAdviceEntity;
import com.iitm.hosteldine.model.mess.MessMasterEntity;

import lombok.Data;

@Data
public class GuestCouponsDto {
    private List<GuestCouponPaymentAdviceEntity> couponRequest;
    private List<GuestCouponMappingsEntity> couponMapping;
    private List<MessMasterEntity> messMaster;
}
