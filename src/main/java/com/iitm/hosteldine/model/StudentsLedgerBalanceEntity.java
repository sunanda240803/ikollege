package com.iitm.hosteldine.model;

import com.iitm.hosteldine.constant.ModelConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

/**
 * Mapping for DB view
 */
@Getter
@Setter
@Entity
@Immutable
@Table(name = "students_ledger_balance", schema = ModelConstants.SCHEMA)
public class StudentsLedgerBalanceEntity {
    @Id
    @Column(name = "acchead", length = Integer.MAX_VALUE)
    private String acchead;

    @Column(name = "credit_amt")
    private Double creditAmt;

    @Column(name = "debit_amt")
    private Double debitAmt;

    @Column(name = "net_bal")
    private Double netBal;
}