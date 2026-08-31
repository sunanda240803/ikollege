package com.iitm.hosteldine.entity.mess;

import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "\"MESS_LEDGER_B\"", schema = "schooldev")
public class MessLedgerBEntity extends CommonEntity {
    @EmbeddedId
    private MessLedgerBEntityId id;

    @Column(name = "voucher_date")
    private LocalDate voucherDate;

    @Size(max = 45)
    @Column(name = "acchead", length = 45)
    private String acchead;

    @Size(max = 32)
    @Column(name = "sub_account_head", length = 32)
    private String subAccountHead;

    @Size(max = 200)
    @Column(name = "description", length = 200)
    private String description;

    @Size(max = 200)
    @Column(name = "description1", length = 200)
    private String description1;

    @Size(max = 32)
    @Column(name = "cheque_no", length = 32)
    private String chequeNo;

    @Column(name = "cheque_date")
    private LocalDate chequeDate;

    @Size(max = 32)
    @Column(name = "doc_ref_no", length = 32)
    private String docRefNo;

    @Column(name = "amount")
    private Double amount;

    @Size(max = 100)
    @Column(name = "rp", length = 100)
    private String rp;

    @Size(max = 100)
    @Column(name = "adv", length = 100)
    private String adv;

    @Size(max = 1)
    @Column(name = "adj", length = 1)
    private String adj;

    @Size(max = 1)
    @Column(name = "recon", length = 1)
    private String recon;

    @Size(max = 1)
    @Column(name = "cancel_status", length = 1)
    private String cancelStatus;

    @Size(max = 5)
    @Column(name = "fc_no", length = 5)
    private String fcNo;

    @Size(max = 8)
    @Column(name = "po_no", length = 8)
    private String poNo;

    @Size(max = 1)
    @Column(name = "debit_or_credit", length = 1)
    private String debitOrCredit;

    @Size(max = 5)
    @Column(name = "reg_no", length = 5)
    private String regNo;

    @Size(max = 5)
    @Column(name = "chal_no", length = 5)
    private String chalNo;

    @Size(max = 4)
    @Column(name = "a2no", length = 4)
    private String a2no;

    @Column(name = "tds_value")
    private Double tdsValue;

    @Column(name = "adv_amt")
    private Double advAmt;

    @Column(name = "bill_amt")
    private Double billAmt;

    @Column(name = "doc_dt")
    private LocalDate docDt;

    @Column(name = "link")
    private Integer link;

    @Column(name = "match_date")
    private LocalDate matchDate;

    @Column(name = "surcharge")
    private Double surcharge;

    @Column(name = "unmatch_amnt")
    private Double unmatchAmnt;
    
}