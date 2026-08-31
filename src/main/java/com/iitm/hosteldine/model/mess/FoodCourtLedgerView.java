package com.iitm.hosteldine.model.mess;

import com.iitm.hosteldine.constant.ModelConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Mapping for DB view
 */

@Data
@Entity
@Table(name = "\"FOOD_COURT_LEDGER_VIEW\"", schema = ModelConstants.SCHEMA)
public class FoodCourtLedgerView {

    @Id
    @Column(name = "student_id", length = 16)
    private String studentId;

    @Column(name = "mess_period_id")
    private Integer messPeriodId;

    @Column(name = "fc_mess_id")
    private Integer fcMessId;

    @Column(name = "total_purchase_amount")
    private Double totalPurchaseAmount;

    @Column(name = "total_credit_amount")
    private Double totalCreditAmount;

    @Column(name = "balance_amount")
    private Double balanceAmount;

    @Column(name = "status", length = Integer.MAX_VALUE)
    private String status;

}