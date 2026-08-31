package com.iitm.hosteldine.mapper.OtherCandidate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.dto.OtherCandidate.TempAccomPaymentTransactionDto;
import com.iitm.hosteldine.model.OtherCandidate.TempAccomPaymentTransactionEntity;

@Mapper
public interface TempAccomPaymentTransactionMapper {
    TempAccomPaymentTransactionMapper INSTANCE = Mappers.getMapper(TempAccomPaymentTransactionMapper.class);

    @Mapping(target = ".", source = ".")
    TempAccomPaymentTransactionDto fromTempAccomPaymentTransactionEntity(TempAccomPaymentTransactionEntity model);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    TempAccomPaymentTransactionEntity toTempAccomPaymentTransactionEntity(TempAccomPaymentTransactionDto modelDto);
    
   
    public static TempAccomPaymentTransactionEntity mapToTransactionEntity(TempAccomPaymentTransactionEntity transaction,Map<String, Object> map) {

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
        transaction.setModifiedAt(LocalDateTime.now());
        transaction.setRetryCount(transaction.getRetryCount()!=null ? transaction.getRetryCount()+1 : 0);

        return transaction;
    }
    
    
}