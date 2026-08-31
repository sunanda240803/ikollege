package com.iitm.hosteldine.dto.mess;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * DTO for {@link com.iitm.hosteldine.entity.mess.MessLedgerAEntity}
 */
@Data
public class MessLedgerADto implements Serializable {
    String createdBy;
    LocalDateTime createdAt;
    String modifiedBy;
    LocalDateTime modifiedAt;
    String activeFlag;
    MessLedgerAIdDto id;
    LocalDate voucherDate;
    String acchead;
    String subAccountHead;
    String description;
    String description1;
    String chequeNo;
    LocalDate chequeDate;
    Double amount;
    String rp;
    String adv;
    String adj;
    String recon;
    String cancelStatus;
    String usr;
    String fcNo;
    String poNo;
    String docRefNo;
    String a2no;
    String party;
    String debitOrCredit;
    Double itAmount;
    Double itPer;
    String itCons;
    String itAcc;
    Integer link;
    LocalDate matchDate;
    Double surcharge;
    Double unmatchAmnt;
    String mrNo;
    LocalDate mrDate;
    String screenType;
    Integer studentCount;
}