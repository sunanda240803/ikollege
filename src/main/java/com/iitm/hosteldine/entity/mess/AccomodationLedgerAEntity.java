package com.iitm.hosteldine.entity.mess;

import java.time.LocalDate;

import com.iitm.hosteldine.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"IIT_PS_TEMP_ACCOM_LEDGER_A\"", schema = "schooldev")
public class AccomodationLedgerAEntity extends CommonEntity {
	
    @EmbeddedId
    private AccomodationLedgerAEntityId id;

    @Column(name = "payment_ref_id")
    private Long paymentRefId;
    
    @Column(name = "voucher_date")
    private LocalDate voucherDate;

    @Column(name = "candidate_id")
    private Long candidateId;
    
    @Column(name = "request_id")
    private Long requestId;

    @Size(max = 200)
    @Column(name = "description", length = 200)
    private String description;

    @Size(max = 200)
    @Column(name = "description1", length = 200)
    private String description1;

    @Column(name = "amount")
    private Double amount;

    @Size(max = 1)
    @Column(name = "cancel_status", length = 1)
    private String cancelStatus;

    @Size(max = 32)
    @Column(name = "doc_ref_no", length = 32)
    private String docRefNo;

    @Size(max = 1)
    @Column(name = "debit_or_credit", length = 1)
    private String debitOrCredit;

}
