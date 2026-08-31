package com.iitm.hosteldine.entity.mess;

import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "\"MESS_LEDGER_A\"", schema = "schooldev")
public class MessLedgerAEntity extends CommonEntity {
    @EmbeddedId
    private MessLedgerAEntityId id;

    @Column(name = "voucher_date")
    private LocalDate voucherDate;

    @Size(max = 45)
    @Column(name = "acchead", length = 45)
    private String acchead;

    @Size(max = 45)
    @Column(name = "sub_account_head", length = 45)
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

    @Size(max = 15)
    @Column(name = "usr", length = 15)
    private String usr;

    @Size(max = 5)
    @Column(name = "fc_no", length = 5)
    private String fcNo;

    @Size(max = 8)
    @Column(name = "po_no", length = 8)
    private String poNo;

    @Size(max = 32)
    @Column(name = "doc_ref_no", length = 32)
    private String docRefNo;

    @Size(max = 4)
    @Column(name = "a2no", length = 4)
    private String a2no;

    @Size(max = 5)
    @Column(name = "party", length = 5)
    private String party;

    @Size(max = 1)
    @Column(name = "debit_or_credit", length = 1)
    private String debitOrCredit;

    @Column(name = "it_amount")
    private Double itAmount;

    @Column(name = "it_per")
    private Double itPer;

    @Size(max = 1)
    @Column(name = "it_cons", length = 1)
    private String itCons;

    @Size(max = 1)
    @Column(name = "it_acc", length = 1)
    private String itAcc;

    @Column(name = "link")
    private Integer link;

    @Column(name = "match_date")
    private LocalDate matchDate;

    @Column(name = "surcharge")
    private Double surcharge;

    @Column(name = "unmatch_amnt")
    private Double unmatchAmnt;

    @Size(max = 5)
    @Column(name = "mr_no", length = 5)
    private String mrNo;

    @Column(name = "mr_date")
    private LocalDate mrDate;

    @Size(max = 128)
    @Column(name = "screen_type", length = 128)
    private String screenType;

    @Column(name = "student_count")
    private Integer studentCount;

}