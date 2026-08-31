package com.iitm.hosteldine.dto.OtherCandidate;

import java.time.LocalDateTime;
import java.time.LocalDate;

import com.iitm.hosteldine.model.OtherCandidate.CandidateAppointmentRequestEntity;
import com.iitm.hosteldine.model.hostel.HostelFloorMasterEntity;
import com.iitm.hosteldine.model.hostel.HostelRoomInfoEntity;
import com.iitm.hosteldine.model.mess.MessMasterEntity;

import lombok.Data;

@Data
public class TempAccomPaymentAdviceDto {
    private Long id;
    private CandidateAppointmentRequestEntity candidateRequest;
    private Long categoryId;
    private String paymentDummyNo;
    private LocalDate hostelPayFromDate;
    private LocalDate hostelPayToDate;
    private Integer hostelTotalNoOfDays;
    private Integer hostelRatePerDay;
    private HostelFloorMasterEntity hostelFloorMaster;
    private HostelRoomInfoEntity hostelRoomInfo;
    private String seat;
    private LocalDate messPayFromDate;
    private LocalDate messPayToDate;
    private Integer noOfBreakfastCoupons;
    private Integer noOfLunchCoupons;
    private Integer noOfDinnerCoupons;
    private Integer breakfastCouponRate;
    private Integer lunchCouponRate;
    private Integer dinnerCouponRate;
    private Long totalBreakfastAmount;
    private Long totalLunchAmount;
    private Long totalDinnerAmount;
    private Long overallAmount;
    private MessMasterEntity messMaster;
    private String paymentStatus;
    private String paymentType1;
    private String paymentReferenceNo1;
    private Integer paymentAmount1;
    private LocalDateTime paymentDate1;
    private String cardStatus;
    private Integer cardCharges;
    private Integer paymentAmount2;
    private String paymentType2;
    private String paymentReferenceNo2;
    private LocalDateTime paymentDate2;
    private Integer messaccAmount;
    private String paymentApprovalStatus;
    
    private String onlinePaymentStatus;
    private String encrypId;
    private String orderNo;
    private Long requestId;
    private String categoryName;
    private int slNo;
    private String paymentDate;
    private LocalDate requestedStayFrom;
    private LocalDate requestedStayTo;
    private Long balanceAmount;
    private Long candidateId;
    private Long messId;
    private String pdfUrl;
    private String hostelViewUrl;
    private String hostelName;
    private Long hostelId;
    private Long roomId;
    private String roomNo;
    private String messName;
    private String messHead;
    private String email;
    private LocalDateTime paymentApprovalDate;

}