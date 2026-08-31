package com.iitm.hosteldine.model.mess;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "\"FOOD_COURT_LEDGER\"", schema = ModelConstants.SCHEMA)
public class FoodCourtLedgerEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fc_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentDetailsInfoEntity student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fc_mess_id", nullable = false)
    private MessMasterEntity messMaster;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "purchase_timestamp")
    private LocalDateTime purchaseTimestamp;

    @Column(name = "debit_or_credit", length = Integer.MAX_VALUE)
    private String debitOrCredit;

    @Column(name = "mess_period_id", nullable = false)
    private Long messPeriodId;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Size(max = 32)
    @Column(name = "item", length = 32)
    private String item;

    @Size(max = 16)
    @Column(name = "mess_billing_no", length = 16)
    private String messBillingNo;

    @Size(max = 64)
    @Column(name = "terminal_ip", length = 64)
    private String terminalIp;

}