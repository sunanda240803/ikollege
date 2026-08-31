package com.iitm.hosteldine.dto.mess;

import com.iitm.hosteldine.constant.DateUtility;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class MessAllottedDinedDto {
    private LocalDate fromDate;
    private LocalDate toDate;
    private String messName;
    private Long messId;
    private Long messPeriodId;
    private Double rate;
    private Long totalDays;
    private Long studentsAlloted;
    private Integer sessionTotalCount;
    private Integer dayTotalCount;
    private String fromDateString;
    private String toDateString;

    // Constructor from two Object arrays (one from each query)
    public MessAllottedDinedDto(Object[] periodDetails, Object[] attendanceSummary) {
        // First query results

        this.fromDate = periodDetails[0] != null ? ((java.sql.Date) periodDetails[0]).toLocalDate() : null;
        this.toDate = periodDetails[1] != null ? ((java.sql.Date) periodDetails[1]).toLocalDate() : null;
        this.messName = (String) periodDetails[2];  // Assuming this can't be null
        this.messId = periodDetails[3] != null ? ((Number) periodDetails[3]).longValue() : null;
        this.messPeriodId = periodDetails[4] != null ? ((Number) periodDetails[4]).longValue() : null;
        this.rate = periodDetails[5] != null ? ((Number) periodDetails[5]).doubleValue() : null;
        this.totalDays = periodDetails[6] != null ? ((Number) periodDetails[6]).longValue() : 0L;
        this.studentsAlloted = periodDetails[7] != null ? ((Number) periodDetails[7]).longValue() : 0L;

        // Second query results
        this.sessionTotalCount = attendanceSummary[0] != null ? ((Number) attendanceSummary[0]).intValue() : 0;
        this.dayTotalCount = attendanceSummary[1] != null ? ((Number) attendanceSummary[1]).intValue() : 0;

        this.fromDateString = this.fromDate != null ? DateUtility.formatDate(this.fromDate) : null;
        this.toDateString = this.toDate != null ? DateUtility.formatDate(this.toDate) : null;
    }

}
