package com.iitm.hosteldine.mapper.paymentGatewayCC;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewWithSettlementEntity;
import com.iitm.hosteldine.model.student.ConvocationAccommodationEntity;
import com.iitm.hosteldine.model.studentDashboard.HostelNightPaymentTransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.dto.OtherCandidate.CandidateProfileDto;
import com.iitm.hosteldine.dto.paymentGatewayCC.PaymentGatewayCcavenueDto;
import com.iitm.hosteldine.model.OtherCandidate.CandidateProfileEntity;
import com.iitm.hosteldine.model.hostel.GuestCouponOnlinePaymentEntity;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewEntity;


@Mapper(imports = {Constants.class})
public interface PaymentGatewayCcavenueMapper {
    PaymentGatewayCcavenueMapper INSTANCE = Mappers.getMapper(PaymentGatewayCcavenueMapper.class);

    @Mapping(target = "billingName", source = "candidate.candiateFullName")
    @Mapping(target = "billingAddress", source = "candidate.address")
    @Mapping(target = "billingCity", source = "candidate.city")
    @Mapping(target = "billingState", source = "candidate.state")
    @Mapping(target = "billingZip", source = "candidate.pin")
    @Mapping(target = "billingTel", source = "candidate.mobileNumber")
    @Mapping(target = "billingEmail", source = "candidate.email")
    PaymentGatewayCcavenueDto toPGDto(CandidateProfileEntity candidate);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    CandidateProfileEntity toCandidateProfileEntity(CandidateProfileDto modelDto);
    
    
    public static PaymentGatewayCcavenueDto changeToPGDto(Map<String, Object> map) {
    	PaymentGatewayCcavenueDto dto=new PaymentGatewayCcavenueDto();
        // Mapping values from the response to dto
    	dto.setBillingName(Optional.ofNullable((String) map.get("billing_name")).orElse(null));
        dto.setCurrency(Optional.ofNullable((String) map.get("currency")).orElse(null));
        dto.setTotalAmount(Optional.ofNullable((String) map.get("amount")).map(amount -> Double.parseDouble(amount)).orElse(0.0));
        dto.setPaymentMode(Optional.ofNullable((String) map.get("payment_mode")).orElse(null)+" "+Constants.HYPHEN+" "+Optional.ofNullable((String) map.get("card_name")).orElse(null));
        dto.setOrderId(Optional.ofNullable((String) map.get("order_id")).orElse(null));
        dto.setTransactionId(Optional.ofNullable((String) map.get("bank_ref_no"))
        		.filter(refNo -> refNo != null && !refNo.equals("null")).orElse(null));
        dto.setCcavRefNum(Optional.ofNullable((String) map.get("tracking_id")).orElse(null));
        dto.setOrderStatus(Optional.ofNullable((String) map.get("order_status")).orElse(null));

        return dto;
    }
    
    //Guest Coupon Online Payment
    
    @Mapping(target = "billingName", source = "student.studentName")
    @Mapping(target = "billingAddress", source = "student.studentAddress")
    @Mapping(target = "billingCity", source = "student.city")
    @Mapping(target = "billingState", source = "student.state")
    @Mapping(target = "billingZip", source = "student.pinCode")
    @Mapping(target = "billingTel", source = "student.studentMobile")
    @Mapping(target = "billingEmail", source = "student.emailId")
    PaymentGatewayCcavenueDto toPGDtoFromStudent(AllStudentsDetailsViewEntity student);

    @Mapping(target = "billingName", source = "student.studentName")
    @Mapping(target = "billingAddress", source = "student.studentAddress")
    @Mapping(target = "billingCity", source = "student.city")
    @Mapping(target = "billingState", source = "student.state")
    @Mapping(target = "billingZip", source = "student.pinCode")
    @Mapping(target = "billingTel", source = "student.studentMobile")
    @Mapping(target = "billingEmail", source = "student.emailId")
    PaymentGatewayCcavenueDto toPGDtoFromStudent_2(AllStudentsDetailsViewWithSettlementEntity student);
    
    
    public static GuestCouponOnlinePaymentEntity mapToTransactionEntity(GuestCouponOnlinePaymentEntity transaction,Map<String, Object> map) {

        // Mapping values from the response to the entity
    	transaction.setNetPayable(Optional.ofNullable((String) map.get("amount"))
    			.filter(amount -> amount != null && !amount.equals("null")).map(Double::parseDouble) .orElse(0.0));
        transaction.setTransactionRefNumber(Optional.ofNullable((String) map.get("bank_ref_no"))
        		.filter(refNo -> refNo != null && !refNo.equals("null")).orElse(null));
        transaction.setCcavReferenceNo(Optional.ofNullable((String) map.get("tracking_id")).orElse(null));
        transaction.setPaymentMethod(Optional.ofNullable((String) map.get("payment_mode")).orElse(null));
        transaction.setPaymentGateway(Optional.ofNullable((String) map.get("card_name")).orElse(null));
        transaction.setReceivedAmount(Optional.ofNullable((String) map.get("amount")).orElse(null));
        transaction.setPaymentStatus(Optional.ofNullable((String) map.get("order_status")).orElse(""));
        transaction.setTransFee(Optional.ofNullable((String) map.get("trans_fee"))
        		.filter(transfee -> transfee != null && !transfee.equals("null")).map(Double::parseDouble) .orElse(0.0));
        transaction.setServiceTax(Optional.ofNullable((String) map.get("service_tax"))
        		.filter(servicefee -> servicefee != null && !servicefee.equals("null")).map(Double::parseDouble) .orElse(0.0));
        transaction.setTransactionDate(DateUtility.parseToLocalDateTime(map.get("trans_date").toString()));
        transaction.setStatusMessage(Optional.ofNullable((String) map.get("status_message")).orElse(null));
        transaction.setModifiedAt(DateUtility.getNowTimeInstant());
        transaction.setRetryCount(transaction.getRetryCount()!=null ? transaction.getRetryCount()+1 : 0);

        return transaction;
    }

    static HostelNightPaymentTransactionEntity mapToHostelNightTransactionEntity(HostelNightPaymentTransactionEntity transaction, Map<String, Object> map) {
        // Mapping values from the response to the entity
        transaction.setPaidAmount(Optional.ofNullable((String) map.get("amount"))
                .filter(amount -> amount != null && !amount.equals("null")).map(Double::parseDouble) .orElse(0.0));
        transaction.setTransactionRefNumber(Optional.ofNullable((String) map.get("bank_ref_no"))
                .filter(refNo -> refNo != null && !refNo.equals("null")).orElse(null));
        transaction.setCcavReferenceNo(Optional.ofNullable((String) map.get("tracking_id")).orElse(null));
        transaction.setPaymentType(Optional.ofNullable((String) map.get("payment_mode")).orElse(null)+Constants.HYPHEN
                +Optional.ofNullable((String) map.get("card_name")).orElse(null));
        transaction.setPaymentStatus(Optional.ofNullable((String) map.get("order_status")).orElse(""));
        transaction.setTransFee(Optional.ofNullable((String) map.get("trans_fee"))
                .filter(transfee -> transfee != null && !transfee.equals("null")).map(Double::parseDouble) .orElse(0.0));
        transaction.setServiceTax(Optional.ofNullable((String) map.get("service_tax"))
                .filter(servicefee -> servicefee != null && !servicefee.equals("null")).map(Double::parseDouble) .orElse(0.0));
        transaction.setPaymentDate(LocalDate.now());
        transaction.setStatusMessage(Optional.ofNullable((String) map.get("status_message")).orElse(null));
        transaction.setModifiedAt(DateUtility.getNowTimeInstant());
        transaction.setRetryCount(transaction.getRetryCount()!=null ? transaction.getRetryCount()+1 : 0);

        return transaction;
    }

    static ConvocationAccommodationEntity mapToConvocationTransactionEntity(ConvocationAccommodationEntity transaction, Map<String, Object> map) {
        // Mapping values from the response to the entity
        transaction.setPaymentAmount(Optional.ofNullable((String) map.get("amount"))
                .filter(amount -> amount != null && !amount.equals("null")).map(Double::parseDouble) .orElse(0.0));
        transaction.setPaymentReferenceNo(Optional.ofNullable((String) map.get("bank_ref_no"))
                .filter(refNo -> refNo != null && !refNo.equals("null")).orElse(null));
        transaction.setCcavReferenceNo(Optional.ofNullable((String) map.get("tracking_id")).orElse(null));
        transaction.setPaymentType(Optional.ofNullable((String) map.get("payment_mode")).orElse(null)+Constants.HYPHEN
                +Optional.ofNullable((String) map.get("card_name")).orElse(null));
        transaction.setPaymentStatus(Optional.ofNullable((String) map.get("order_status")).orElse(""));
        transaction.setTransFee(Optional.ofNullable((String) map.get("trans_fee"))
                .filter(transfee -> transfee != null && !transfee.equals("null")).map(Double::parseDouble) .orElse(0.0));
        transaction.setServiceTax(Optional.ofNullable((String) map.get("service_tax"))
                .filter(servicefee -> servicefee != null && !servicefee.equals("null")).map(Double::parseDouble) .orElse(0.0));
        transaction.setPaymentDate(LocalDateTime.now());
        transaction.setStatusMessage(Optional.ofNullable((String) map.get("status_message")).orElse(null));
        transaction.setModifiedAt(DateUtility.getNowTimeInstant());
        transaction.setRetryCount(transaction.getRetryCount()!=null ? transaction.getRetryCount()+1 : 0);

        return transaction;
    }
}