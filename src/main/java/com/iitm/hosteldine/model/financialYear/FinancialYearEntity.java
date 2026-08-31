package com.iitm.hosteldine.model.financialYear;

import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "\"FINANCIAL_YEAR\"", schema = "schooldev")
public class FinancialYearEntity extends CommonEntity {
    @Id
    @Size(max = 16)
    @Column(name = "fin_year", nullable = false, length = 16)
    private String finYear;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "last_transaction_date")
    private LocalDate lastTransactionDate;

    @Column(name = "balance_sheet_finalised")
    private Boolean balanceSheetFinalised;

    @NotNull
    @Column(name = "companyid", nullable = false)
    private Integer companyid;

    @Column(name = "current_finyear", length = Integer.MAX_VALUE)
    private String currentFinyear;

}