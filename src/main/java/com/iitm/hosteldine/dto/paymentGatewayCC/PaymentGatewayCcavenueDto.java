package com.iitm.hosteldine.dto.paymentGatewayCC;

import com.iitm.hosteldine.dto.hostel.GuestCouponIssuedDTO;
import lombok.Data;

import java.util.List;

@Data
public class PaymentGatewayCcavenueDto {
    private String transactionId;
    private Long merchantId;
    private String orderId;
    private String currency;
    private Double totalAmount;
    private String actionUrl;
    private String redirectUrl;
    private String cancelUrl;
    private String language;
    private String billingName;
    private String billingAddress;
    private String billingCity;
    private String billingState;
    private String billingZip;
    private String billingCountry;
    private String billingTel;
    private String billingEmail;
    private String additionalInfo;
    private String encRequest;
    private String accessCode;
    private String orderStatus;
    private String paymentMode;
    private String ccavRefNum;
    private String errorStatus;
    private List<GuestCouponIssuedDTO> errorList;
}