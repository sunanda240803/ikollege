package com.iitm.hosteldine.model;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "\"SETTLEMENT_HISTORY\"", schema = ModelConstants.SCHEMA)
public class SettlementHistoryEntity extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settlement_id", nullable = false)
    private Long settlementId;

    @Column(name = "old_settlement_id")
    private String oldSettlementId;

    @Column(name = "student_id")
    private String studentId;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "debit_or_credit")
    private String debitOrCredit;

    @Column(name = "settlement_date")
    private LocalDate settlementDate;

    @Column(name = "description")
    private String description;

}