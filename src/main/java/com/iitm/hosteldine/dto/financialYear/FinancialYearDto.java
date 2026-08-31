package com.iitm.hosteldine.dto.financialYear;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.iitm.hosteldine.model.financialYear.FinancialYearEntity}
 */
@Data
@Builder
public class FinancialYearDto implements Serializable {
    private String activeFlag;
    private String finYear;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate lastTransactionDate;
    private Boolean balanceSheetFinalised;
    private Integer companyid;
    private String currentFinyear;
}